/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.intellij.analysis;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.encoding.EncodingProjectManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonarlint.intellij.SonarTest;
import org.sonarlint.intellij.core.ProjectBindingManager;
import org.sonarlint.intellij.core.SonarLintFacade;
import org.sonarlint.intellij.ui.SonarLintConsole;
import org.sonarsource.sonarlint.core.client.api.common.analysis.ClientInputFile;
import org.sonarsource.sonarlint.core.client.api.common.analysis.IssueListener;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SonarLintAnalyzerTest extends SonarTest {
  @Mock
  private ProjectBindingManager projectBindingManager;
  @Mock
  private EncodingProjectManager encodingProjectManager;
  @Mock
  private SonarLintConsole console;
  @Mock
  private ModuleRootManager moduleRootManager;
  @Mock
  private AnalysisConfigurator configurator;
  @Mock
  private Module module;
  @Mock
  private SonarLintFacade facade;

  private SonarLintAnalyzer analyzer;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    analyzer = new SonarLintAnalyzer(projectBindingManager, encodingProjectManager, console);

    when(projectBindingManager.getFacadeForAnalysis()).thenReturn(facade);
    when(moduleRootManager.getContentEntries()).thenReturn(new ContentEntry[0]);

    super.register(module, ModuleRootManager.class, moduleRootManager);
    super.register(module, AnalysisConfigurator.class, configurator);
    super.registerEP(AnalysisConfigurator.EP_NAME, AnalysisConfigurator.class);
  }

  @Test
  public void testAnalysis() {
    VirtualFile file = mock(VirtualFile.class);
    when(file.getPath()).thenReturn("testFile");
    IssueListener listener = mock(IssueListener.class);

    analyzer.analyzeModule(module, Collections.singleton(file), listener);
    verify(facade).startAnalysis(anyListOf(ClientInputFile.class), eq(listener), anyMapOf(String.class, String.class));
  }
}
