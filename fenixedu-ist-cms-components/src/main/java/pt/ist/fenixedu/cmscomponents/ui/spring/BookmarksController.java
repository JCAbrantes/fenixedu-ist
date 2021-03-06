/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST CMS Components.
 *
 * FenixEdu IST CMS Components is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST CMS Components is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST CMS Components.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.cmscomponents.ui.spring;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.cms.domain.Category;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(accessGroup = "logged", app = NewsController.class, title = "bookmarks.title")
@RequestMapping("learning/bookmarks")
public class BookmarksController {

    @RequestMapping
    public String bookmarks(Model model) {
        model.addAttribute(
                "bookmarks",
                Authenticate.getUser().getBookmarksSet().stream()
                        .sorted(Comparator.comparing(cat -> cat.getSite().getName().getContent())).collect(Collectors.toList()));
        final Student student = AccessControl.getPerson().getStudent();
        if (student != null) {
            model.addAttribute(
                    "courses",
                    student.getActiveRegistrationsIn(ExecutionSemester.readActualExecutionSemester())
                            .stream()
                            .flatMap(
                                    registration -> registration.getAttendingExecutionCoursesForCurrentExecutionPeriod().stream())
                            .collect(Collectors.toList()));
        }
        return "fenix-learning/bookmarks";
    }

    @RequestMapping("/add/{category}")
    public RedirectView addBookmark(@PathVariable Category category) {
        FenixFramework.atomic(() -> {
            Authenticate.getUser().addBookmarks(category);
        });
        return new RedirectView("/learning/bookmarks", true);
    }

    @RequestMapping("/remove/{category}")
    public RedirectView removeBookmark(@PathVariable Category category) {
        FenixFramework.atomic(() -> {
            Authenticate.getUser().removeBookmarks(category);
        });
        return new RedirectView("/learning/bookmarks", true);
    }

}
