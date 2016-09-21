/**
 * Copyright 2010-2016 Boxfuse GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flywaydb.core.internal.util.scanner;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;
import org.flywaydb.core.internal.util.Location;
import org.flywaydb.core.internal.util.scanner.classpath.ClassPathResource;
import org.flywaydb.core.internal.util.scanner.classpath.ResourceAndClassScanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ScannerTest {

    @Mock
    private ResourceAndClassScanner mockScanner;

    @Test
    public void usesClassPathScannerByDefault() throws Exception {
        Location location = new Location("classpath:migration/sql");
        Scanner scanner = new Scanner(this.getClass().getClassLoader());

        Resource[] resources = scanner.scanForResources(location, "V", "sql");
        Class<?>[] classes = scanner.scanForClasses(new Location("classpath:org/flywaydb/core/internal/resolver/jdbc/dummy"), JdbcMigration.class);

        assertEquals(3, classes.length);
        assertEquals(4, resources.length);
    }

    @Test
    public void usesClassPathScannerAndAnyPassedInScanners() throws Exception {
        Location location = new Location("classpath:migration/sql");
        Location classLocation = new Location("classpath:org/flywaydb/core/internal/resolver/jdbc/dummy");
        given(mockScanner.scanForResources(location, "V", "sql"))
                .willReturn(new Resource[] { new ClassPathResource("loc", this.getClass().getClassLoader()) });
        given(mockScanner.scanForClasses(classLocation, JdbcMigration.class))
                .willReturn(new Class[] { Object.class });
        Scanner scanner = new Scanner(this.getClass().getClassLoader(), mockScanner);

        Resource[] resources = scanner.scanForResources(location, "V", "sql");
        Class<?>[] classes = scanner.scanForClasses(classLocation, JdbcMigration.class);

        assertEquals(4, classes.length);
        assertEquals(5, resources.length);
    }

    @Test
    public void returnsEmptyArrayWhenNothingFound() throws Exception {
        Scanner scanner = new Scanner(this.getClass().getClassLoader());

        Resource[] resources = scanner.scanForResources(new Location("classpath:DOES_NOT_EXIST"), "V", "sql");
        Class<?>[] classes = scanner.scanForClasses(new Location("classpath:DOES_NOT_EXIST"), JdbcMigration.class);

        assertEquals(0, classes.length);
        assertEquals(0, resources.length);
    }
}
