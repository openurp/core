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

package org.openurp.edu.grade.app.model

import org.beangle.data.model.LongId
import org.openurp.code.edu.model.ExamStatus
import org.openurp.code.edu.model.GradeType
import org.openurp.base.edu.model.Course
import org.openurp.base.model.Project
import org.openurp.base.model.Semester
import org.openurp.base.std.model.Student

import GradeModifyApply._
import scala.compiletime.uninitialized

object GradeModifyApply {

  object GradeModifyStatus extends Enumeration {

    val NOT_AUDIT = of("未审核")

    val DEPART_AUDIT_PASSED = of("院系审核通过")

    val DEPART_AUDIT_UNPASSED = of("院系审核未通过")

    val ADMIN_AUDIT_PASSED = of("院长审核通过")

    val ADMIN_AUDIT_UNPASSED = of("院长审核未通过")

    val FINAL_AUDIT_PASSED = of("最终审核通过")

    val FINAL_AUDIT_UNPASSED = of("最终审核未通过")

    val GRADE_DELETED = of("成绩已被删除")

    private def of(name: String): GradeModifyStatus = {
      new GradeModifyStatus(name)
    }

    class GradeModifyStatus extends Val {
      var fullName: String = uninitialized
      def this(fullName: String) = {
        this()
        this.fullName = fullName
      }
    }

    import scala.language.implicitConversions
    implicit def convertValue(v: Value): GradeModifyStatus = v.asInstanceOf[GradeModifyStatus]
  }
}

@SerialVersionUID(-4325413107423926231L)
class GradeModifyApply extends LongId {

  var std: Student = uninitialized

  var semester: Semester = uninitialized

  var project: Project = uninitialized

  var course: Course = uninitialized

  /** 成绩类型 */
  var gradeType: GradeType = uninitialized

  /** 考试情况 */
  var examStatus: ExamStatus = uninitialized

  /** 原考试情况 */
  var examStatusBefore: ExamStatus = uninitialized

  /** 原得分 */
  var origScore: java.lang.Float = uninitialized

  /** 原得分字面值 */
  var origScoreText: String = uninitialized

  /** 得分 */
  var score: java.lang.Float = uninitialized

  /** 得分字面值 */
  var scoreText: String = uninitialized

  /** 审核状态 */
  var status: GradeModifyStatus.Value = GradeModifyStatus.NOT_AUDIT

  /** 申请理由 */
  var applyReason: String = uninitialized

  /** 审核理由 */
  var auditReason: String = uninitialized

  /** 申请人 */
  var applyer: String = uninitialized

  /** 审核人 */
  var auditer: String = uninitialized

  /** 最终审核人 */
  var finalAuditer: String = uninitialized

  def hasChange(): Boolean = {
    if (this.score == null || this.origScore == null) {
      return this.score != this.origScore || this.examStatus != this.examStatusBefore
    }
    this.score != this.origScore || this.examStatus != this.examStatusBefore
  }
}
