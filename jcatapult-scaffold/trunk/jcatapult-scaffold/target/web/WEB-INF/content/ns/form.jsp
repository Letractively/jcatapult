<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<h2>Beans</h2>
<s:form action="${param['action']}" method="POST" theme="css_xhtml">
  <h3>Bean</h3>
  <div class="notice">
    Notice here.
  </div>
  <!-- Save off the information for updates and deletes -->
  <s:hidden name="ids" value="%{bean.id}"/>
  <s:hidden name="bean.id"/>

  <s:textfield key="bean.age" required="false"/>
  <s:textfield key="bean.name" required="true"/>
  <s:textfield key="bean.inner.innerInner.first" required="false"/>
  <s:textfield key="bean.inner.innerInner.second" required="false"/>
  <s:textfield key="bean.inner.lastName" required="false"/>
  <s:textfield key="bean.inner.firstName" required="true"/>
  <s:textfield key="bean.notSaved" required="false"/>
  <s:textfield key="bean.optional" required="false"/>
  <s:checkbox key="bean.flag" required="false"/>

  <s:action name="prepare" id="prepare"/>
  <s:select list="%{#prepare.emails}" key="emailId" listKey="id" listValue="value" required="true"/>
  <s:checkboxlist list="%{#prepare.categories}" key="categoriesIds" listKey="id" listValue="name" required="true"/>

  <s:submit name="Save" value="Save"/>
  <c:if test="${param['action'] == 'update'}">
    <s:submit action="delete" value="Delete"/>
  </c:if>
  <s:submit name="redirect-action:index" value="Cancel"/>

</s:form>