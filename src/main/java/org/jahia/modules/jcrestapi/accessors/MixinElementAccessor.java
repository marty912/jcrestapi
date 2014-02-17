/**
 * This file is part of Jahia, next-generation open source CMS:
 * Jahia's next-generation, open source CMS stems from a widely acknowledged vision
 * of enterprise application convergence - web, search, document, social and portal -
 * unified by the simplicity of web content management.
 *
 * For more information, please visit http://www.jahia.com.
 *
 * Copyright (C) 2002-2013 Jahia Solutions Group SA. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL (or any later version), you may redistribute this Program in connection
 * with Free/Libre and Open Source Software ("FLOSS") applications as described
 * in Jahia's FLOSS exception. You should have received a copy of the text
 * describing the FLOSS exception, and it is also available here:
 * http://www.jahia.com/license
 *
 * Commercial and Supported Versions of the program (dual licensing):
 * alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms and conditions contained in a separate
 * written agreement between you and Jahia Solutions Group SA.
 *
 * If you are unsure which license is appropriate for your use,
 * please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.jcrestapi.accessors;

import org.jahia.modules.jcrestapi.model.JSONMixin;
import org.jahia.modules.jcrestapi.model.JSONMixins;
import org.jahia.modules.jcrestapi.model.JSONNode;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

/**
 * @author Christophe Laprun
 */
public class MixinElementAccessor extends ElementAccessor<JSONMixins, JSONMixin, JSONNode> {
    @Override
    protected JSONMixins getSubElementContainer(Node node) throws RepositoryException {
        return new JSONMixins(getParentFrom(node), node);
    }

    @Override
    protected JSONMixin getSubElement(Node node, String subElement) throws RepositoryException {
        final NodeType mixin = getMixin(node, subElement);
        if (mixin == null) {
            return null;
        }

        return new JSONMixin(node, mixin);
    }

    @Override
    protected JSONMixin delete(Node node, String subElement) throws RepositoryException {
        node.removeMixin(subElement);
        return null;
    }

    @Override
    protected CreateOrUpdateResult<JSONMixin> createOrUpdate(Node node, String subElement, JSONNode childData) throws RepositoryException {
        // if the node doesn't already have the mixin, add it
        final boolean isUpdate = !node.isNodeType(subElement);
        if (isUpdate) {
            node.addMixin(subElement);
        }

        // retrieve node type associated with mixin
        NodeType mixin = getMixin(node, subElement);

        // we now need to use the rest of the given child data to add / update the parent node content
        NodeElementAccessor.initNodeFrom(node, childData);

        return new CreateOrUpdateResult<JSONMixin>(isUpdate, new JSONMixin(node, mixin));
    }

    private NodeType getMixin(Node node, String subElement) throws RepositoryException {
        NodeType mixin = null;
        final NodeType[] mixinNodeTypes = node.getMixinNodeTypes();
        for (NodeType mixinNodeType : mixinNodeTypes) {
            if (mixinNodeType.getName().equals(subElement)) {
                mixin = mixinNodeType;
                break;
            }
        }
        return mixin;
    }
}