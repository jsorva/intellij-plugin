package fi.aalto.cs.apluscourses.model;

import fi.aalto.cs.apluscourses.utils.CoursesClient;

public interface Authentication extends CoursesClient.HttpAuthentication {

  void persist();

  void clear();
}