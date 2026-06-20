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

package org.openurp.edu.grade.service.filters

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.commons.script.ExprEvaluator
import org.openurp.edu.grade.domain.GradeFilter
import org.openurp.edu.grade.model.CourseGrade

class ScriptGradeFilter extends GradeFilter {

  var script: String = _

  var exprEvaluator: ExprEvaluator = _

  def this(script: String, exprEvaluator: ExprEvaluator) = {
    this()
    this.script = script
    this.exprEvaluator = exprEvaluator
  }

  override def filter(grades: Iterable[CourseGrade]): Iterable[CourseGrade] = {
    if (Strings.isEmpty(script)) return grades
    val newGrades = Collections.newBuffer[CourseGrade]
    for (grade <- grades) {
      val params = new java.util.HashMap[String, AnyRef]
      params.put("grade", grade)
      val rs = exprEvaluator.eval(script, params, classOf[java.lang.Boolean])
      if (rs.booleanValue) newGrades += grade
    }
    newGrades
  }

}
