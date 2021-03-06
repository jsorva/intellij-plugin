package fi.aalto.cs.apluscourses.ui.exercise;

import com.intellij.ui.SimpleListCellRenderer;
import fi.aalto.cs.apluscourses.model.Group;
import javax.swing.JList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GroupRenderer extends SimpleListCellRenderer<Group> {

  @Override
  public void customize(@NotNull JList<? extends Group> list,
                        @Nullable Group group,
                        int index,
                        boolean selected,
                        boolean hasFocus) {
    if (group == null) {
      setText("Select group");
    } else {
      setText(String.join(", ", group.getMemberNames()));
    }
  }
}
