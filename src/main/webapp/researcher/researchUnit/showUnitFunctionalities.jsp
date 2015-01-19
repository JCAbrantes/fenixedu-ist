<%--

    Copyright © ${project.inceptionYear} Instituto Superior Técnico

    This file is part of Fenix IST.

    Fenix IST is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Fenix IST is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ page language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:xhtml/>
<bean:define id="unitID" name="unit" property="externalId"/>
<bean:define id="unitExternalId" name="unit" property="externalId"/>

<h2><fr:view name="unit" property="name"/> (<fr:view name="unit" property="acronym"/>)</h2>

<ul>
	<li>
		<html:link page="<%= "/sendEmailToResearchUnitGroups.do?method=prepare&unitExternalId=" + unitExternalId + "&unitId=" + unitExternalId %>">
			<bean:message key="label.sendEmailToGroups" bundle="RESEARCHER_RESOURCES"/>
		</html:link>
		<br/>
		<span class="color888">
			<bean:message key="label.sendEmailToGroups.explanation" bundle="RESEARCHER_RESOURCES"/>
		</span>
	</li>
	<c:if test="${unit.site.canAdminGroup.isMember(LOGGED_USER_ATTRIBUTE)}">
		<li>
			<html:link page="<%= "/researchUnitFunctionalities.do?method=configureGroups&unitId=" + unitID %>">
				<bean:message key="label.configurePersistentGroups" bundle="RESEARCHER_RESOURCES"/>
			</html:link>
			<br/>
			<span class="color888">
				<bean:message key="label.configurePersistentGroups.explanation" bundle="RESEARCHER_RESOURCES"/>
			</span>
		</li>
	</c:if>
	<li>
		<html:link page="<%= "/researchUnitFunctionalities.do?method=manageFiles&unitId=" + unitID %>">
			<bean:message key="label.manageFiles" bundle="RESEARCHER_RESOURCES"/>
		</html:link>
		<br/>
		<span class="color888">
			<bean:message key="label.manageFiles.explanation" bundle="RESEARCHER_RESOURCES"/>
		</span>
	</li>

</ul>
