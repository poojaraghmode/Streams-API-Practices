module com.github.streams {
  requires org.junit.jupiter;
  requires org.junit.platform.commons;
  requires net.datafaker;
  requires static lombok;

  // LEARN
  opens com.github.streams.learn.functional_interfaces to
      org.junit.platform.commons;
  opens com.github.streams.learn.functional_interfaces.solutions to
      org.junit.platform.commons;

  // PRACTICE EASY
  opens com.github.streams.practice.a_easy.numbers.problems to
      org.junit.platform.commons;
  opens com.github.streams.practice.a_easy.strings.problems to
      org.junit.platform.commons;
  opens com.github.streams.practice.b_medium.employee to
      org.junit.platform.commons;

  //  PRACTICE MEDIUM
  opens com.github.streams.practice.b_medium.strings to
      org.junit.platform.commons;
  opens com.github.streams.practice.b_medium.numbers to
      org.junit.platform.commons;
  opens com.github.streams.practice.b_medium.numbers.problems to
      org.junit.platform.commons;
  opens com.github.streams.practice.b_medium.strings.problems to
      org.junit.platform.commons;

  // PRACTICE HARD
  opens com.github.streams.practice.c_hard.numbers to
      org.junit.platform.commons;
  opens com.github.streams.practice.c_hard.numbers.problems to
      org.junit.platform.commons;
  opens com.github.streams.practice.b_medium to
      org.junit.platform.commons;

  // INTERVIEW QUESTIONS
  opens com.github.streams.interview.collections to
      org.junit.platform.commons;
  opens com.github.streams.interview.algorithms to
      org.junit.platform.commons;
  opens com.github.streams.interview.streams to
      org.junit.platform.commons;
  opens com.github.streams.interview.generics to
      org.junit.platform.commons;
  opens com.github.streams.interview.concurrency to
      org.junit.platform.commons;
}
