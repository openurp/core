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

package org.openurp.std.graduation.service.impl

import org.beangle.commons.lang.Doubles
import org.beangle.data.dao.EntityDao
import org.openurp.edu.grade.model.StdGpa
import org.openurp.edu.program.model.Program
import org.openurp.std.graduation.domain.DegreeAuditChecker
import org.openurp.std.graduation.model.DegreeResult

class DegreeAuditGpaChecker extends DegreeAuditChecker {
  var entityDao: EntityDao = _
  var defaultGpa: Double = 2.0d

  override def check(result: DegreeResult, program: Program): (Boolean, String) = {
    val std = result.std
    entityDao.findBy(classOf[StdGpa], "std", std).headOption match
      case None => (false, "查不到平均绩点")
      case Some(stat) =>
        val gpa = stat.gpa.doubleValue
        result.gpa = Some(stat.gpa.doubleValue)
        result.wms = Some(stat.wms.doubleValue)
        val standard = program.degreeGpa.getOrElse(defaultGpa)
        if (Doubles.compare(standard, gpa, 1e-6) <= 0) {
          (true, s"${gpa}")
        } else {
          (false, s"${gpa}")
        }
  }

}
