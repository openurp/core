/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.edu.grade.service.audit

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.time.Weeks
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.base.edu.model.Course
import org.openurp.base.model.Semester
import org.openurp.code.edu.model.{ExamType, GradeType}
import org.openurp.edu.exam.model.ExamTaker
import org.openurp.edu.grade.domain.{AuditPlanContext, AuditPlanListener}
import org.openurp.edu.grade.model.*

import java.time.LocalDate
import scala.compiletime.uninitialized

class AuditExamTakerListener extends AuditPlanListener {

  var entityDao: EntityDao = uninitialized

  override def end(context: AuditPlanContext): Unit = {
    if (context.result.passed) return

    // 审核未通过时，找出补缓考成绩尚未生效的考试记录，用于给相关课程加"未出补缓考成绩"备注
    // 情况一：该班次课下完全没有补考/缓考总评成绩（成绩未录入或未计算）
    val builder1 = OqlBuilder.from(classOf[ExamTaker], "et").where("et.std=:std", context.std)
    builder1.where("et.examType.id in(:makeupTypeIds)", Seq(ExamType.Makeup, ExamType.Delay))
    builder1.where(s"not exists(from ${classOf[GaGrade].getName} gg where " +
      " gg.courseGrade.clazz=et.clazz and gg.courseGrade.std=et.std" +
      s" and gg.gradeType.id in(${GradeType.MakeupGa},${GradeType.DelayGa}))")
    val examTakers1 = entityDao.search(builder1)

    // 情况二：补考/缓考总评已判通过但尚未发布，审核尚未采信，视同成绩未出。
    // 注意：仅取 passed=true。不及格且未发布的总评不影响审核结果，
    // 若也写入 pendingWay 会被误判为"预计可通过"，失去预测意义。
    val builder2 = OqlBuilder.from(classOf[ExamTaker], "et").where("et.std=:std", context.std)
    builder2.where("et.examType.id in(:makeupTypeIds)", Seq(ExamType.Makeup, ExamType.Delay))
    builder2.where(s"exists(from ${classOf[GaGrade].getName} gg where " +
      " gg.courseGrade.clazz=et.clazz and gg.courseGrade.std=et.std" +
      s" and gg.gradeType.id in(${GradeType.MakeupGa},${GradeType.DelayGa}) and gg.passed=true and gg.status != ${Grade.Status.Published})")
    val examTakers2 = entityDao.search(builder2)

    // 两组条件互斥（not exists vs exists），合并不会产生重复记录
    val examTakers = examTakers1 ++ examTakers2
    if (examTakers.nonEmpty) {
      val today = LocalDate.now
      // 只取半年（约25周）以内的补缓考记录，过期考试不再提示
      val examCourses = Collections.newMap[Course, Semester]
      examTakers.filter(x => Math.abs(Weeks.between(x.semester.endOn, today)) <= 25) foreach { taker =>
        examCourses.getOrElseUpdate(taker.clazz.course, taker.semester)
      }
      for (groupResult <- context.result.groupResults) {
        for (car <- groupResult.courseResults) {
          if (!car.passed && examCourses.keySet.contains(car.course)) {
            val semester = examCourses(car.course)
            car.addRemark(s"未出补缓考成绩(${semester.schoolYear}学年${semester.name}学期)")
            car.pendingWay = Some(CoursePendingWay.Makeup)
            groupResult.addCourseResult(car)
          }
        }
      }
    }
  }

}
