/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// package org.apache.jackrabbit.core.query.lucene;
package edu.northwestern.jcr.adapter.fedora.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;
import org.apache.jackrabbit.spi.commons.conversion.NameParser;
import org.apache.jackrabbit.spi.commons.conversion.DefaultNamePathResolver;
import org.apache.jackrabbit.spi.commons.conversion.ParsingPathResolver;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.conversion.PathResolver;
import org.apache.jackrabbit.spi.commons.namespace.NamespaceResolver;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;

import javax.jcr.NamespaceException;

/**
 * <code>NamePathResolverImpl</code>...
 */
public class NamePathResolverImpl extends DefaultNamePathResolver {

    private static Logger log = LoggerFactory.getLogger(NamePathResolverImpl.class);

    private static final NameFactory NAME_FACTORY = NameFactoryImpl.getInstance();
    private static final PathFactory PATH_FACTORY = PathFactoryImpl.getInstance();

    private NamePathResolverImpl(NameResolver nResolver, PathResolver pResolver) {
        super(nResolver, pResolver);
    }

    public static NamePathResolver create(NamespaceMappings nsMappings) {
        NameResolver nResolver = new NameResolverImpl(nsMappings);
        PathResolver pResolver = new ParsingPathResolver(PATH_FACTORY, nResolver);
        return new NamePathResolverImpl(nResolver, pResolver);
    }


    //--------------------------------------------------------< inner class >---
    /**
     * Query specific NameResolver that does not assume an empty prefix for the
     * default namespace URI. Instead the prefix is always retrieved from the
     * NamespaceResolver.
     */
    private static class NameResolverImpl implements NameResolver {

        /**
         * Namespace resolver.
         */
        private final NamespaceResolver resolver;

        /**
         * Creates a parsing name resolver.
         *
         * @param resolver namespace resolver
         */
        public NameResolverImpl(NamespaceResolver resolver) {
            this.resolver = resolver;
        }

        //-------------------------------------------------------< NameResolver >---
        /**
         * Parses the prefixed JCR name and returns the resolved qualified name.
         *
         * @param name prefixed JCR name
         * @return qualified name
         * @throws IllegalNameException if the JCR name format is invalid
         * @throws NamespaceException if the namespace prefix can not be resolved
         */
        public Name getQName(String name) throws IllegalNameException, NamespaceException {
            return NameParser.parse(name, resolver, NAME_FACTORY);
        }

        /**
         * Returns the prefixed JCR name for the given qualified name.
         * Note, that the JCR prefix is always retrieved from the NamespaceResolver
         * even if the name is in the defaut namespace. This is a special treatement
         * for query specific implementation, which defines a prefix for all namespace
         * URIs including the default namespace.
         *
         * @param name qualified name
         * @return prefixed JCR name
         * @throws NamespaceException if the namespace URI can not be resolved
         */
        public String getJCRName(Name name) throws NamespaceException {
            String uri = name.getNamespaceURI();
            if (resolver.getPrefix(uri).length() == 0) {
                return name.getLocalName();
            } else {
                return resolver.getPrefix(uri) + ":" + name.getLocalName();
            }
        }
    }

}
