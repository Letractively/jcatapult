<%--
  ~ Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  ~ either express or implied. See the License for the specific
  ~ language governing permissions and limitations under the License.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="jc" uri="http://www.jcatapult.org/mvc/1.0/tags" %>
<html>
<head><title>Scope result page</title></head>
<body>
  <p>
    Result is ${simpleFormBean.firstName} ${simpleFormBean.lastName}
  </p>
  <form action="simple-form" method="POST">
    <jc:text name="simpleFormBean.firstName" class="red"/>
    <jc:text name="simpleFormBean.lastName"/>
    <input type="submit" value="Submit"/>
  </form>
</body>
</html>