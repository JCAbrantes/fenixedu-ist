/**
 * Copyright © 2013 Instituto Superior Técnico
 *
 * This file is part of FenixEdu IST Integration.
 *
 * FenixEdu IST Integration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu IST Integration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu IST Integration.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.ist.fenixedu.integration.ui.struts.action.person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Department;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.organizationalStructure.PartyTypeEnum;
import org.fenixedu.academic.domain.organizationalStructure.PedagogicalCouncilUnit;
import org.fenixedu.academic.domain.organizationalStructure.ScientificCouncilUnit;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitUtils;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;

import pt.ist.fenixedu.integration.ui.struts.action.MessagingFilesApp;
import pt.ist.fenixedu.integration.ui.struts.action.commons.UnitFunctionalities;

@StrutsFunctionality(app = MessagingFilesApp.class, path = "view", titleKey = "label.files.view")
@Mapping(module = "messaging", path = "/viewFiles")
@Forwards(value = { @Forward(name = "uploadFile", path = "/commons/unitFiles/uploadFile.jsp"),
        @Forward(name = "manageFiles", path = "/commons/unitFiles/manageFiles.jsp"),
        @Forward(name = "showSources", path = "/messaging/files/showSources.jsp"),
        @Forward(name = "editFile", path = "/commons/unitFiles/editFile.jsp"),
        @Forward(name = "editUploaders", path = "/commons/PersistentMemberGroups/configureUploaders.jsp"),
        @Forward(name = "managePersistedGroups", path = "/commons/PersistentMemberGroups/managePersistedGroups.jsp"),
        @Forward(name = "editPersistedGroup", path = "/commons/PersistentMemberGroups/editPersistedGroup.jsp"),
        @Forward(name = "createPersistedGroup", path = "/commons/PersistentMemberGroups/createPersistedGroup.jsp") })
public class ViewFilesDA extends UnitFunctionalities {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setAttribute("functionalityAction", "viewFiles");
        request.setAttribute("module", "messaging");
        return super.execute(mapping, form, request, response);
    }

    @EntryPoint
    public ActionForward showSources(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        List<PersonFileSource> result = new ArrayList<PersonFileSource>();

        LocalizedString departmentsName =
                new LocalizedString().with(org.fenixedu.academic.util.LocaleUtils.PT, "Departamentos").with(org.fenixedu.academic.util.LocaleUtils.EN,
                        "Departments");
        PersonFileSourceGroupBean departmentsGroup = new PersonFileSourceGroupBean(departmentsName);

        SortedSet<Department> departments = new TreeSet<Department>(Department.COMPARATOR_BY_NAME);
        departments.addAll(Bennu.getInstance().getDepartmentsSet());
        for (Department department : departments) {
            departmentsGroup.add(new PersonFileSourceBean(department.getDepartmentUnit()));
        }

        LocalizedString researchUnitsName =
                new LocalizedString().with(org.fenixedu.academic.util.LocaleUtils.PT, "Unidades de Investigação").with(org.fenixedu.academic.util.LocaleUtils.EN,
                        "Research Units");
        PersonFileSourceGroupBean researchUnitsGroup = new PersonFileSourceGroupBean(researchUnitsName);

        SortedSet<Unit> researchUnits = new TreeSet<Unit>(Unit.COMPARATOR_BY_NAME_AND_ID);
        researchUnits.addAll(UnitUtils.readAllActiveUnitsByType(PartyTypeEnum.RESEARCH_UNIT));
        for (Unit unit : researchUnits) {
            researchUnitsGroup.add(new PersonFileSourceBean(unit));
        }

        LocalizedString scientificAreaName =
                new LocalizedString().with(org.fenixedu.academic.util.LocaleUtils.PT, "Áreas Ciêntificas").with(org.fenixedu.academic.util.LocaleUtils.EN,
                        "Scientific Areas");
        PersonFileSourceGroupBean scientificAreaUnits = new PersonFileSourceGroupBean(scientificAreaName);

        SortedSet<Unit> scientificAreas = new TreeSet<Unit>(Unit.COMPARATOR_BY_NAME_AND_ID);
        scientificAreas.addAll(UnitUtils.readAllActiveUnitsByType(PartyTypeEnum.SCIENTIFIC_AREA));
        for (Unit unit : scientificAreas) {
            scientificAreaUnits.add(new PersonFileSourceBean(unit));
        }

        PersonFileSourceBean pedagogicalCouncil = new PersonFileSourceBean(PedagogicalCouncilUnit.getPedagogicalCouncilUnit());

        PersonFileSourceBean scientific = new PersonFileSourceBean(ScientificCouncilUnit.getScientificCouncilUnit());

        result.add(departmentsGroup);
        result.add(researchUnitsGroup);
        result.add(scientificAreaUnits);
        result.add(pedagogicalCouncil);
        result.add(scientific);

        Collections.sort(result, PersonFileSource.COMPARATOR);

        filterSources(result, getLoggedPerson(request));

        request.setAttribute("sources", result);

        return mapping.findForward("showSources");
    }

    private void filterSources(List<PersonFileSource> result, Person person) {
        Iterator<PersonFileSource> iterator = result.iterator();

        while (iterator.hasNext()) {
            PersonFileSource source = iterator.next();

            if (source.getCount() == 0 && !source.isAllowedToUpload(person)) {
                iterator.remove();
            } else {
                filterSources(source.getChildren(), person);
            }
        }
    }

    public ActionForward viewFiles(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return manageFiles(mapping, form, request, response);
    }

}
