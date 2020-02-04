package fi.aalto.cs.intellij.presentation.module;

import fi.aalto.cs.intellij.model.Module;
import fi.aalto.cs.intellij.presentation.base.ListElementModel;
import fi.aalto.cs.intellij.utils.ObservableProperty;
import java.awt.font.TextAttribute;
import org.jetbrains.annotations.NotNull;

public class ModuleListElementModel extends ListElementModel<Module> {

  protected final StateObserver stateObserver = new StateObserver();

  public ModuleListElementModel(@NotNull Module module) {
    super(module);
    module.state.addValueChangedObserver(stateObserver);
  }

  public String getName() {
    return getModel().getName();
  }

  public String getUrl() {
    return getModel().getUrl().toString();
  }

  public String getStatus() {
    switch (getModel().state.get()) {
      case Module.NOT_INSTALLED:
      case Module.FETCHED:
        return "Not installed";
      case Module.FETCHING:
        return "Downloading...";
      case Module.LOADING:
        return "Installing...";
      case Module.LOADED:
      case Module.INSTALLED:
        return "Installed";
      default:
        return "Error";
    }
  }

  public float getFontWeight() {
    return getModel().state.get() >= Module.LOADED
        ? TextAttribute.WEIGHT_BOLD
        : TextAttribute.WEIGHT_REGULAR;
  }

  private class StateObserver implements ObservableProperty.ValueChangedObserver<Integer> {

    @Override
    public void valueChanged(Integer value) {
      changed();
    }
  }
}
