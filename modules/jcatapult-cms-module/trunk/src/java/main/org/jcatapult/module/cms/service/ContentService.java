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
package org.jcatapult.module.cms.service;

import java.util.Locale;
import javax.persistence.PersistenceException;

import org.jcatapult.module.cms.domain.ContentNode;
import org.jcatapult.module.cms.domain.ContentType;
import org.jcatapult.module.cms.domain.NodeAction;
import org.jcatapult.module.cms.domain.PageNode;
import org.jcatapult.module.cms.domain.SiteNode;
import org.jcatapult.module.user.domain.DefaultUser;

import com.google.inject.ImplementedBy;

/**
 * <p>
 * This interface defines the operations that are available for
 * dealing with content.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(DefaultContentService.class)
public interface ContentService {

    /**
     * Locates the site with the given name. This will return the site, even if it has been deleted.
     *
     * @param   name The name of the site.
     * @return  The site or null if the site doesn't exist in the database.
     */
    SiteNode findSite(String name);

    /**
     * Locates the page for the given site and URI. This will return the page, even if it has been
     * deleted.
     *
     * @param   site The site.
     * @param   uri The URI of the page.
     * @return  The page or null if the page doesn't exist in the database.
     */
    PageNode findPage(String site, String uri);

    /**
     * Locates a global content node that is associated to the given site rather than a page. This
     * will return the content node, even if it has been deleted.
     *
     * @param   site The site.
     * @param   name The name of the content node.
     * @return  The content node or null if it doesn't exist in the database.
     */
    ContentNode findGlobalContent(String site, String name);

    /**
     * Locates the content node for the given site and page. This will return the content node, even
     * if it has been deleted.
     *
     * @param   site The site.
     * @param   uri The URI of the page.
     * @param   name The name of the content node.
     * @return  The content node or null if it doesn't exist in the database.
     */
    ContentNode findPageContent(String site, String uri, String name);

    /**
     * This method attempts to resolve the given site. It has some special handling for the site,
     * pages and the content nodes below the pages. If the site is not visible, this will throw an
     * InvisibleException. If the site is visible, this will ensure that the page and content nodes
     * in the site are only those that are visible.
     *
     * @param   site The site.
     * @return  The SiteNode if it exists in the database and is visible, otherwise null if it doesn't
     *          exist in the database.
     * @throws  InvisibleException If the site is invisible.
     */
//    SiteNode findVisibleSiteWithVisiblePagesAndVisibleContent(String site) throws InvisibleException;

    /**
     * This method attempts to resolve the given page for the given site. It has some special handling
     * for the site, page and the content nodes below the page. If the site is not visible, this will
     * throw the InvisibleException. The same holds true if the page is not visible. If the site and
     * page are both visible, this will ensure that the content nodes in the page are only the content
     * that is currently visible.
     *
     * @param   site The site.
     * @param   uri The URI of the page.
     * @return  The PageNode if it exists in the database and is visible, otherwise null if it doesn't
     *          exist in the database.
     * @throws  InvisibleException If the page or site is invisible.
     */
//    PageNode findVisiblePageWithVisibleContent(String site, String uri) throws InvisibleException;

    /**
     * This method attempts to resolve the given page for the given site at a specific point in time.
     * It has some special handling for the site, page and the content nodes below the page. If the
     * site is not visible at the given point in time, this will throw an InvisibleException. The
     * same holds true if the page is not visible. If the site and page are both visible, this will
     * ensure that the content nodes in the page are only the content that were visible at the given
     * point in time.
     *
     * @param   site The site.
     * @param   uri The URI of the page.
     * @param   dateTime The point in time.
     * @return  The PageNode if it exists in the database and is visible, otherwise null if it doesn't
     *          exist in the database.
     * @throws  InvisibleException If the page or site is invisible.
     */
//    PageNode findHistoricalPageWithVisibleContent(String site, String uri, DateTime dateTime) throws InvisibleException;

    /**
     * Creates a new site with the given name. If the site already exists, this will not create the
     * site and instead just return a result that indicates nothing happened. The handling of the
     * creation depends on the given user. The creation might immediately take effect, or it might
     * be put into a pending state and require approval.
     *
     * @param   site The site to create.
     * @param   user The currently logged in user.
     * @return  The result from the creation.
     * @throws  PersistenceException If the site was created in the split second after this method
     *          checks for it and then inserts it. Very rare, but possible.
     */
    CreateResult<SiteNode> createSite(String site, DefaultUser user) throws PersistenceException;

    /**
     * Creates a new page for the given URI and site. If the site doesn't exist, this method will
     * attempt to create it by calling the {@link #createSite(String, DefaultUser)} method. If the
     * page already exists, this will not create the page and instead just return a result that
     * indicates nothing happened. The handling of the creation depends on the given user. The
     * creation might immediately take effect, or it might be put into a pending state and require
     * approval.
     *
     * @param   site The site.
     * @param   uri The URI of the page to create.
     * @param   user The currently logged in user.
     * @return  The result from the creation.
     * @throws  PersistenceException If the page or site were created in the split second after this
     *          method or the createSite method checks for it and then inserts it. Very rare, but
     *          possible.
     */
    CreateResult<PageNode> createPage(String site, String uri, DefaultUser user);

    /**
     * Creates or updates content for the page with the given URI and site. If the page doesn't exist,
     * this method will attempt to create it by calling the {@link #createPage(String, String, DefaultUser)}
     * method. If the content node already exists and contains visible content for the given locale,
     * this will not create the content node and instead just return a result that indicates nothing
     * happened. The handling of the creation depends on the given user. The creation might
     * immediately take effect, or it might be put into a pending state and require approval.
     *
     * @param   site The site.
     * @param   uri The URI of the page.
     * @param   name The name of the content node.
     * @param   locale The locale of the content.
     * @param   content The content itself.
     * @param   type The type of the content.
     * @param   dynamic This flag determines if the content node is dynamic meaning that it was added
     *          by the user to a page (the page doesn't have to be dynamic). It this case, if the
     *          node is added and the user doesn't have publish permissions the node will be pending
     *          as will the content itself. However, if this is false, the content node is created
     *          an is immediately visible while the content itself might still be pending.
     * @param   user The currently logged in user.
     * @return  The result from the creation.
     * @throws  PersistenceException If the content node, page, or site were created in the split
     *          second after this method or the createPage/createSite methods check for it and then
     *          inserts it. Very rare, but possible.
     */
    CreateResult<ContentNode> storeContent(String site, String uri, String name, Locale locale,
        String content, ContentType type, boolean dynamic, DefaultUser user);

    /**
     * Deletes the given site. If the site has already been deleted, this does nothing. The deletion
     * depends on the user given. It might take effect immediately or it might be put into a pending
     * state and require approval.
     *
     * @param   site The site.
     * @param   user The currently logged in user.
     * @return  The result from the delete.
     */
    DeleteResult<SiteNode> deleteSite(String site, DefaultUser user);

    /**
     * Deletes the given page. If the page has already been deleted, this does nothing. The deletion
     * depends on the user given. It might take effect immediately or it might be put into a pending
     * state and require approval.
     *
     * @param   site The site.
     * @param   uri The URI of the page.
     * @param   user The currently logged in user.
     * @return  The result from the delete.
     */
    DeleteResult<PageNode> deletePage(String site, String uri, DefaultUser user);

    /**
     * Deletes the given content node. If the content node has already been deleted, this does
     * nothing. The deletion depends on the user given. It might take effect immediately or it might
     * be put into a pending state and require approval.
     *
     * @param   site The site.
     * @param   uri The URI of the page.
     * @param   name The name of the content node.
     * @param   user The currently logged in user.
     * @return  The result from the delete.
     */
    DeleteResult<ContentNode> deleteContent(String site, String uri, String name, DefaultUser user);

    /**
     * Approves the given action. The approval process depends on the type of action that was taken
     * and the type of node that the action was taken on.
     *
     * @param   action The action taken.
     * @param   user The currently logged in user that is doing the approval.
     * @param   comment A comment to go with the approval.
     * @return  The result of the approval.
     */
    ApproveResult approve(NodeAction action, DefaultUser user, String comment);

    /**
     * Rejects the given action. The rejection process depends on the type of action that was taken
     * and the type of node that the action was taken on.
     *
     * @param   action The action taken.
     * @param   user The currently logged in user that is doing the rejection.
     * @param   comment A comment about the rejection.
     * @return  The result of the rejection.
     */
    RejectResult reject(NodeAction action, DefaultUser user, String comment);

    /**
     * Declines the given action. The decline process depends on the type of action that was taken
     * and the type of node that the action was taken on.
     *
     * @param   action The action taken.
     * @param   user The currently logged in user that is doing the decline.
     * @param   comment A comment about the decline.
     * @return  The result of the decline.
     */
    DeclineResult decline(NodeAction action, DefaultUser user, String comment);
}