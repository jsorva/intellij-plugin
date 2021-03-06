package fi.aalto.cs.apluscourses.intellij.toolwindows;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import fi.aalto.cs.apluscourses.intellij.actions.ActionGroups;
import fi.aalto.cs.apluscourses.intellij.actions.ActionUtil;
import fi.aalto.cs.apluscourses.intellij.actions.ImportModuleAction;
import fi.aalto.cs.apluscourses.intellij.services.PluginSettings;
import fi.aalto.cs.apluscourses.presentation.MainViewModel;
import fi.aalto.cs.apluscourses.ui.exercise.ExercisesView;
import fi.aalto.cs.apluscourses.ui.module.ModulesView;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class APlusToolWindowFactory extends BaseToolWindowFactory implements DumbAware {

  @Override
  protected JComponent createToolWindowContentInternal(@NotNull Project project) {
    ModulesView modulesView = createModulesView(project);
    ExercisesView exercisesView = createExercisesView(project);
    JBSplitter splitter = new JBSplitter(true);
    splitter.setFirstComponent(modulesView.getBasePanel());
    splitter.setSecondComponent(exercisesView.getBasePanel());
    return splitter;
  }

  @NotNull
  private static ModulesView createModulesView(@NotNull Project project) {
    ModulesView modulesView = new ModulesView();
    PluginSettings.getInstance().getMainViewModel(project).courseViewModel
        .addValueObserver(modulesView, ModulesView::viewModelChanged);

    ActionManager actionManager = ActionManager.getInstance();
    ActionGroup group = (ActionGroup) actionManager.getAction(ActionGroups.MODULE_ACTIONS);

    ActionToolbar toolbar = actionManager.createActionToolbar(ActionPlaces.TOOLBAR, group, true);
    toolbar.setTargetComponent(modulesView.moduleListView);
    modulesView.toolbarContainer.add(toolbar.getComponent());

    ActionPopupMenu popupMenu =
        actionManager.createActionPopupMenu(ActionPlaces.TOOLWINDOW_POPUP, group);
    popupMenu.setTargetComponent(modulesView.moduleListView);
    modulesView.moduleListView.setPopupMenu(popupMenu.getComponent());

    modulesView.moduleListView.addListActionListener(ActionUtil.createOnEventLauncher(
        ImportModuleAction.ACTION_ID, modulesView.moduleListView));

    return modulesView;
  }

  @NotNull
  private static ExercisesView createExercisesView(@NotNull Project project) {
    ExercisesView exercisesView = new ExercisesView();

    MainViewModel mainViewModel = PluginSettings.getInstance().getMainViewModel(project);
    mainViewModel.exercisesViewModel
        .addValueObserver(exercisesView, ExercisesView::viewModelChanged);

    ActionManager actionManager = ActionManager.getInstance();
    ActionGroup group = (ActionGroup) actionManager.getAction(ActionGroups.EXERCISE_ACTIONS);

    ActionToolbar toolbar = actionManager.createActionToolbar(ActionPlaces.TOOLBAR, group, true);
    toolbar.setTargetComponent(exercisesView.getExerciseGroupsTree());
    exercisesView.toolbarContainer.add(toolbar.getComponent());

    return exercisesView;
  }

}
