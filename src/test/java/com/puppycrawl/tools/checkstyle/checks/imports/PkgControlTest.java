////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.imports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class PkgControlTest {
    private final PkgControl pcRoot = new PkgControl("com.kazgroup.courtlink", false);
    private final PkgControl pcCommon = new PkgControl(pcRoot, "common", false);

    @Before
    public void setUp() {
        pcRoot.addImportRule(
            new PkgImportRule(false, false, "org.springframework", false, false));
        pcRoot.addImportRule(
            new PkgImportRule(false, false, "org.hibernate", false, false));
        pcRoot.addImportRule(
            new PkgImportRule(true, false, "org.apache.commons", false, false));

        pcCommon.addImportRule(
            new PkgImportRule(true, false, "org.hibernate", false, false));
    }

    @Test
    public void testLocateFinest() {
        assertEquals(pcRoot, pcRoot
                .locateFinest("com.kazgroup.courtlink.domain"));
        assertEquals(pcCommon, pcRoot
                .locateFinest("com.kazgroup.courtlink.common.api"));
        assertNull(pcRoot.locateFinest("com"));
    }

    @Test
    public void testEnsureTrailingDot() {
        assertNull(pcRoot.locateFinest("com.kazgroup.courtlinkkk"));
        assertNull(pcRoot.locateFinest("com.kazgroup.courtlink/common.api"));
    }

    @Test
    public void testCheckAccess() {
        assertEquals(AccessResult.DISALLOWED, pcCommon.checkAccess(
                "org.springframework.something",
                "com.kazgroup.courtlink.common"));
        assertEquals(AccessResult.ALLOWED, pcCommon
                .checkAccess("org.apache.commons.something",
                        "com.kazgroup.courtlink.common"));
        assertEquals(AccessResult.DISALLOWED, pcCommon.checkAccess(
                "org.apache.commons", "com.kazgroup.courtlink.common"));
        assertEquals(AccessResult.ALLOWED, pcCommon.checkAccess(
                "org.hibernate.something", "com.kazgroup.courtlink.common"));
        assertEquals(AccessResult.DISALLOWED, pcCommon.checkAccess(
                "com.badpackage.something", "com.kazgroup.courtlink.common"));
        assertEquals(AccessResult.DISALLOWED, pcRoot.checkAccess(
                "org.hibernate.something", "com.kazgroup.courtlink"));
    }

    @Test
    public void testUnknownPkg() {
        assertNull(pcRoot.locateFinest("net.another"));
    }
}
