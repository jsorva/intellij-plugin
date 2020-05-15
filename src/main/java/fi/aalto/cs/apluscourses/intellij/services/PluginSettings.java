package fi.aalto.cs.apluscourses.intellij.services;

import static fi.aalto.cs.apluscourses.intellij.services.PluginSettings.LocalSettingsNames.A_PLUS_SHOW_REPL_CONFIGURATION_DIALOG;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import fi.aalto.cs.apluscourses.presentation.MainViewModel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginSettings implements MainViewModelProvider {

  private static PluginSettings instance;

  private PluginSettings() {

  }

  public interface LocalSettingsNames {

    String A_PLUS_SHOW_REPL_CONFIGURATION_DIALOG = "A+.showReplConfigDialog";
  }

  public static final String COURSE_CONFIGURATION_FILE_URL
      = "https://grader.cs.hut.fi/static/O1_2020/projects/o1_course_config.json";
  private static final PropertiesComponent propertiesManager = PropertiesComponent.getInstance();

  @NotNull
  private final ConcurrentMap<Project, MainViewModel> mainViewModels = new ConcurrentHashMap<>();

  private final ProjectManagerListener projectManagerListener = new ProjectManagerListener() {
    @Override
    public void projectClosed(@NotNull Project project) {
      mainViewModels.remove(project);
      ProjectManager.getInstance().removeProjectManagerListener(project, projectManagerListener);
    }
  };

  /**
   * Methods to get the Singleton instance of {@link PluginSettings}.
   *
   * @return an instance of {@link PluginSettings}.
   */
  @NotNull
  public static PluginSettings getInstance() {
    if (instance == null) {
      instance = new PluginSettings();
    }
    return instance;
  }

  /**
   * Returns a MainViewModel for a specific project.
   *
   * @param project A project, or null (equivalent for default project).
   * @return A main view model.
   */
  @NotNull
  public MainViewModel getMainViewModel(@Nullable Project project) {
    if (project == null || !project.isOpen()) {
      // If project is closed, use default project to avoid creation of main view models, that would
      // never be cleaned up.
      project = ProjectManager.getInstance().getDefaultProject();
    }
    return mainViewModels.computeIfAbsent(project, this::createNewMainViewModel);
  }

  @NotNull
  private MainViewModel createNewMainViewModel(@NotNull Project project) {
    ProjectManager.getInstance().addProjectManagerListener(project, projectManagerListener);
    return new MainViewModel();
  }

  public boolean shouldShowReplConfigurationDialog() {
    return Boolean.parseBoolean(propertiesManager.getValue(A_PLUS_SHOW_REPL_CONFIGURATION_DIALOG));
  }

  /**
   * Method to set property, responsible for showing REPL configuration window.
   *
   * @param showReplConfigDialog a boolean value of the flag.
   */
  public void setShowReplConfigurationDialog(boolean showReplConfigDialog) {
    propertiesManager
        //  a String explicitly
        .setValue(A_PLUS_SHOW_REPL_CONFIGURATION_DIALOG, String.valueOf(showReplConfigDialog));
  }

  /**
   * Method that checks if the value is set (exists/non-empty etc.) and sets the {@link String}
   * value to 'true'.
   */
  public void initiateLocalSettingShowReplConfigurationDialog() {
    if (!propertiesManager.isValueSet(A_PLUS_SHOW_REPL_CONFIGURATION_DIALOG)) {
      propertiesManager.setValue(A_PLUS_SHOW_REPL_CONFIGURATION_DIALOG, String.valueOf(true));
    }
  }
}
