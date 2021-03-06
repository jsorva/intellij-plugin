package fi.aalto.cs.apluscourses.presentation.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;

public class ListElementViewModelTest {

  @Test
  public void testSetSelected() {
    ListElementViewModel<Object> listElementViewModel = new ListElementViewModel<>(new Object());
    assertFalse("isSelected() should initially return false.", listElementViewModel.isSelected());
    listElementViewModel.setSelected(true);
    assertTrue("isSelected() should return true.", listElementViewModel.isSelected());
    listElementViewModel.setSelected(false);
    assertFalse("isSelected() should return false.", listElementViewModel.isSelected());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testIndexAndListModel() {
    ListElementViewModel<Object> listElementViewModel = new ListElementViewModel<>(new Object());
    assertEquals(0, listElementViewModel.getIndex());

    int index = 44;
    listElementViewModel.setIndex(index);
    assertEquals("getIndex() should return newly set index.",
        index, listElementViewModel.getIndex());

    listElementViewModel.onChanged(); // nothing should happen

    BaseListViewModel<ListElementViewModel<Object>> listViewModel = mock(BaseListViewModel.class);
    listElementViewModel.setListModel(listViewModel);

    listElementViewModel.onChanged();
    verify(listViewModel).onElementChanged(index);

    verifyNoMoreInteractions(listViewModel);
  }
}
