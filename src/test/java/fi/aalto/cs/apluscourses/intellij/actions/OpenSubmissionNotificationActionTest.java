package fi.aalto.cs.apluscourses.intellij.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import fi.aalto.cs.apluscourses.intellij.notifications.Notifier;
import fi.aalto.cs.apluscourses.intellij.notifications.SubmissionRenderingErrorNotification;
import fi.aalto.cs.apluscourses.model.SubmissionResult;
import fi.aalto.cs.apluscourses.model.UrlRenderer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class OpenSubmissionNotificationActionTest {

  private AnActionEvent event;
  private SubmissionResult submissionResult;
  private Notification notification;
  private Notifier notifier;
  private UrlRenderer submissionRenderer;

  /**
   * Called before each test.
   */
  @Before
  public void setUp() {
    event = mock(AnActionEvent.class);
    doReturn(mock(Project.class)).when(event).getProject();
    submissionResult
        = new SubmissionResult(1, SubmissionResult.Status.GRADED, "http://example.com");
    notification = mock(Notification.class);
    notifier = mock(Notifier.class);
    submissionRenderer = mock(UrlRenderer.class);
  }

  @Test
  public void testOpenSubmissionNotificationAction() throws Exception {
    new OpenSubmissionNotificationAction(
        submissionResult,
        submissionRenderer,
        notifier
    ).actionPerformed(event, notification);

    ArgumentCaptor<String> argumentCaptor
        = ArgumentCaptor.forClass(String.class);
    verify(submissionRenderer).show(argumentCaptor.capture());
    assertEquals(submissionResult.getUrl(), argumentCaptor.getValue());
  }

  @Test
  public void testErrorNotification() throws Exception {
    Exception exception = new Exception();
    doThrow(exception).when(submissionRenderer).show(anyString());

    new OpenSubmissionNotificationAction(
        submissionResult,
        submissionRenderer,
        notifier
    ).actionPerformed(event, notification);

    ArgumentCaptor<SubmissionRenderingErrorNotification> argumentCaptor
        = ArgumentCaptor.forClass(SubmissionRenderingErrorNotification.class);
    verify(notifier).notify(argumentCaptor.capture(), any(Project.class));
    assertSame(exception, argumentCaptor.getValue().getException());
  }

}
