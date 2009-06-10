/*
 * Copyright 2004-2005 The Apache Software Foundation or its licensors,
 *                     as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.test.api;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * <code>WorkspaceCopyReferenceableTest</code> contains tests for copying
 * referenceable nodes in one workspace.
 *
 * @test
 * @sources WorkspaceCopyReferenceableTest.java
 * @executeClass org.apache.jackrabbit.test.api.WorkspaceCopyReferenceableTest
 * @keywords level2
 */
public class WorkspaceCopyReferenceableTest extends AbstractWorkspaceReferenceableTest {

    /**
     * Copies of referenceable nodes (nodes with UUIDs) are automatically given
     * new UUIDs.
     */
    public void testCopyNodesNewUUID() throws RepositoryException {
        // add mixin referenceable to node1
        addMixinReferenceableToNode(node1);
        
        // copy referenceable node below non-referenceable node
        String dstAbsPath = node2.getPath() + "/" + node1.getName();
        workspace.copy(node1.getPath(), dstAbsPath);

        // uuid of copied node should be different than original node uuid
        String originalUUID = node1.getUUID();
        Node copiedNode = node2.getNode(node1.getName());
        String copiedUUID = copiedNode.getUUID();

        assertFalse(originalUUID.equals(copiedUUID));
    }
}