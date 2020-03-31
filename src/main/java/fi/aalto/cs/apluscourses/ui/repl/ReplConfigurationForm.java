package fi.aalto.cs.apluscourses.ui.repl;

import static fi.aalto.cs.apluscourses.presentation.ReplConfigurationFormModel.showREPLConfigWindow;
import static java.util.Objects.requireNonNull;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import fi.aalto.cs.apluscourses.presentation.ReplConfigurationFormModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ReplConfigurationForm extends JPanel {

  public static final String INFOLABEL_TEXT = "<html>"
      + "This is an A+ Scala REPL configuration window. By default, the working "
      + "directory and the dependencies, loaded to the REPL classpath belong to the Module, that "
      + "it was started on. To change the behavior, use the checkbox at the bottom of the window."
      + "</html>";

  private ReplConfigurationFormModel model;

  private TextFieldWithBrowseButton workingDirectoryField;
  private ComboBox moduleComboBox;
  private JCheckBox dontShowThisWindowCheckBox;
  private JPanel contentPane;
  private JLabel infoTextLabel;

  public ReplConfigurationForm() {
  }

  public ReplConfigurationForm(ReplConfigurationFormModel model) {
    this.model = model;
    dontShowThisWindowCheckBox.setSelected(!showREPLConfigWindow);
    dontShowThisWindowCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showREPLConfigWindow = !showREPLConfigWindow;
      }
    });

    infoTextLabel.setText(INFOLABEL_TEXT);

    addFileChooser("Choose Working Directory", workingDirectoryField, model.getProject());
    workingDirectoryField.setText(model.getModuleWorkingDirectory());

    model.getModuleNames().forEach(moduleName -> moduleComboBox.addItem(moduleName));
    moduleComboBox.setSelectedItem(model.getTargetModuleName());
    moduleComboBox.setEnabled(true);
    moduleComboBox.setRenderer(new ModuleComboBoxListRenderer());
  }

  private void addFileChooser(
      final String title,
      final TextFieldWithBrowseButton textField,
      final Project project) {
    final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(
        false, true, false,
        false, false, false) {
      @Override
      public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
        return super.isFileVisible(file, showHiddenFiles) && file.isDirectory();
      }
    };
    fileChooserDescriptor.setTitle(title);
    textField.addBrowseFolderListener(title, null, project, fileChooserDescriptor);
  }

  public void updateModel() {
    model.setTargetModuleName(requireNonNull(moduleComboBox.getSelectedItem()).toString());
    model.setModuleWorkingDirectory(workingDirectoryField.getText());
  }

  public JPanel getContentPane() {
    return contentPane;
  }

  public ReplConfigurationFormModel getModel() {
    return model;
  }

  public void setModel(ReplConfigurationFormModel model) {
    this.model = model;
  }

  public TextFieldWithBrowseButton getWorkingDirectoryField() {
    return workingDirectoryField;
  }

  public void setWorkingDirectoryField(
      TextFieldWithBrowseButton workingDirectoryField) {
    this.workingDirectoryField = workingDirectoryField;
  }

  public ComboBox getModuleComboBox() {
    return moduleComboBox;
  }

  public void setModuleComboBox(ComboBox moduleComboBox) {
    this.moduleComboBox = moduleComboBox;
  }

  public JCheckBox getDontShowThisWindowCheckBox() {
    return dontShowThisWindowCheckBox;
  }

  public void setDontShowThisWindowCheckBox(JCheckBox dontShowThisWindowCheckBox) {
    this.dontShowThisWindowCheckBox = dontShowThisWindowCheckBox;
  }

  public void setContentPane(JPanel contentPane) {
    this.contentPane = contentPane;
  }

  public JLabel getInfoTextLabel() {
    return infoTextLabel;
  }

  public void setInfoTextLabel(JLabel infoTextLabel) {
    this.infoTextLabel = infoTextLabel;
  }
}
