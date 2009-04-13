/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 */

/**
 * <h1>Localization keys</h1>
 * <p>
 * These keys can be used to override the default messages inside the
 * application. The keys include error messages, form labels, form
 * validation messages and section headers.
 * </p>
 *
 * <h2>Errors</h2>
 * <dl>
 * <dt>error</dt>
 * <dd>The general error message.</dd>
 * </dl>
 *
 * <h2>Sections</h2>
 * <dt>title</dt>
 * <dd>The title for the HTML page.</dd>
 * <dt>heading</dt>
 * <dd>The heading of the page.</dd>
 * </dl>
 *
 * <h2>Labels</h2>
 * <dl>
 * <dt>user.passwordExpired</dt>
 * <dd>The password expired checkbox label.</dd>
 * <dt>user.expired</dt>
 * <dd>The account expired checkbox label.</dd>
 * <dt>user.locked</dt>
 * <dd>The account locked checkbox label.</dd>
 * <dt>user.deleted</dt>
 * <dd>The account deleted checkbox label.</dd>
 * <dt>associations[roleIds]</dt>
 * <dd>The role checkbox list label.</dd>
 * </dl>
 *
 * <h3>Configuration</h3>
 * <p>
 * Here are the configuration options that control how the user module
 * will display forms and validate data.
 * </p>
 * <dl>
 * <dt>jcatapult.user.fields.name</dt>
 * <dd>This is
 * a boolean configuration element that determines if the first and last name
 * fields should be displayed. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.name-required</dt>
 * <dd>This is
 * a boolean configuration element that determines if the first and last name
 * fields should be required. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.business</dt>
 * <dd>This is
 * a boolean configuration element that determines if the company name
 * field should be displayed. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.business-required</dt>
 * <dd>This is
 * a boolean configuration element that determines if the company name
 * field should be required. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.home-address</dt>
 * <dd>This is
 * a boolean configuration element that determines if the home address form
 * fields should be displayed. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.home-address-required</dt>
 * <dd>This is
 * a boolean configuration element that determines if the home address form
 * fields should be required. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.work-address</dt>
 * <dd>This is
 * a boolean configuration element that determines if the work address form
 * fields should be displayed. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.work-address-required</dt>
 * <dd>This is
 * a boolean configuration element that determines if the work address form
 * fields should be required. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.home-phone</dt>
 * <dd>This is
 * a boolean configuration element that determines if the home phone form
 * field should be displayed. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.home-phone-required</dt>
 * <dd>This is
 * a boolean configuration element that determines if the home phone form
 * field should be required. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.work-phone</dt>
 * <dd>This is
 * a boolean configuration element that determines if the work phone form
 * field should be displayed. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.work-phone-required</dt>
 * <dd>This is
 * a boolean configuration element that determines if the work phone form
 * field should be required. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.cell-phone</dt>
 * <dd>This is
 * a boolean configuration element that determines if the cell phone form
 * field should be displayed. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.cell-phone-required</dt>
 * <dd>This is
 * a boolean configuration element that determines if the cell phone form
 * field should be required. Defaults to <strong>false</strong>.
 * </dd>
 * <dt>jcatapult.user.fields.email-options</dt>
 * <dd>This is
 * a boolean configuration element that determines if the standard email checkboxes
 * should be presented to the user. These are "receive newsletters from Company"
 * and "Receive info from partners". Defaults to <strong>true</strong>.
 * </dd>
 * </dl>
 */
package org.jcatapult.module.user.action;