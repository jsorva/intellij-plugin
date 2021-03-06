package fi.aalto.cs.apluscourses.intellij.model;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import fi.aalto.cs.apluscourses.model.Component;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

public class APlusProjectTest extends BasePlatformTestCase {

  @Test
  public void testGetBasePath() {
    Project project = spy(getProject());
    doReturn(".idea").when(project).getBasePath();

    APlusProject aplusProject = new APlusProject(project);

    Assert.assertEquals("The base path should be correct",
        Paths.get(Project.DIRECTORY_STORE_FOLDER),
        aplusProject.getBasePath());
  }

  @Test
  public void testResolveComponentState() {
    final String loadedComponentName = "loadedModule";
    final String fetchedComponentName = "fetchedModule";
    final String notInstalledComponentName = "notInstalledModule";
    final String errorComponentName = "errorModule";

    APlusProject project = new APlusProject(getProject()) {
      @Override
      public boolean doesDirExist(@NotNull Path relativePath) {
        String pathStr = relativePath.toString();
        return pathStr.equals(loadedComponentName) || pathStr.equals(fetchedComponentName);
      }
    };

    IntelliJModelExtensions.TestComponent loadedComponent =
        new IntelliJModelExtensions.TestComponent(loadedComponentName, new Object());
    Assert.assertEquals(Component.LOADED, project.resolveComponentState(loadedComponent));

    IntelliJModelExtensions.TestComponent fetchedComponent =
        new IntelliJModelExtensions.TestComponent(fetchedComponentName, null);
    Assert.assertEquals(Component.FETCHED, project.resolveComponentState(fetchedComponent));

    IntelliJModelExtensions.TestComponent notInstalledComponent =
        new IntelliJModelExtensions.TestComponent(notInstalledComponentName, null);
    Assert.assertEquals(Component.NOT_INSTALLED,
        project.resolveComponentState(notInstalledComponent));

    IntelliJModelExtensions.TestComponent errorComponent =
        new IntelliJModelExtensions.TestComponent(errorComponentName, new Object());
    Assert.assertEquals(Component.ERROR, project.resolveComponentState(errorComponent));
  }

}
