package fi.aalto.cs.apluscourses.intellij.notifications;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.NotNull;

public class SubmissionRenderingErrorNotification extends Notification {

  @NotNull
  private final Exception exception;

  /**
   * Construct a notification informing the user that an error occurred while attempting to render
   * a submission.
   */
  public SubmissionRenderingErrorNotification(@NotNull Exception exception) {
    super(
        "A+",
        "Failed to open submission",
        "Failed to open submission. Please open the submission in the A+ web interface. Error "
            + "message: '" + exception.getMessage() + "'",
        NotificationType.ERROR
    );
    this.exception = exception;
  }

  @NotNull
  public Exception getException() {
    return exception;
  }

}
