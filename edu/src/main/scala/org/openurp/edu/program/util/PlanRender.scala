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

package org.openurp.edu.program.util

import org.openurp.edu.program.model.{CourseGroup, CoursePlan}

object PlanRender {

  def calcBranchLevel(plan: CoursePlan): Int = {
    val tops = plan.topGroups
    if tops.isEmpty then 0 else tops.map(g => calcBranchLevel(g, 0)).max
  }

  private def calcBranchLevel(group: CourseGroup, fromLevel: Int): Int = {
    if group.children.isEmpty then
      if isLeaf(group) then fromLevel else fromLevel + 1
    else group.children.map(c => calcBranchLevel(c, fromLevel + 1)).max
  }

  def isLeaf(group: CourseGroup): Boolean = {
    if (group.children.isEmpty && group.planCourses.isEmpty) {
      true
    } else if (group.planCourses.isEmpty) {
      false //有子组
    } else {
      //FIXME using Doubles.compare
      if group.planCourses.size > 1 then false //有多门课程
      else group.credits <= group.planCourses.head.credits //只有一门课程
    }
  }
}
