/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.common.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.Transformer;
import org.apache.syncope.common.lib.search.SearchableFields;
import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.EntityTO;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.to.UserTO;

public final class EntityTOUtils {

    public static final List<String> ANY_FIELDS;

    static {
        List<String> anyFields = new ArrayList<>();
        anyFields.addAll(SearchableFields.get(UserTO.class));
        anyFields.addAll(SearchableFields.get(GroupTO.class));
        anyFields.addAll(SearchableFields.get(AnyObjectTO.class));

        ANY_FIELDS = Collections.unmodifiableList(anyFields);
    }

    public static <E extends EntityTO> Transformer<E, String> keyTransformer() {
        return new Transformer<E, String>() {

            @Override
            public String transform(final E input) {
                return input.getKey();
            }
        };
    }

    /**
     * Private default constructor, for static-only classes.
     */
    private EntityTOUtils() {
    }
}
