package ristretto.compiler.plugin;

import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

import java.util.function.Consumer;
import java.util.function.Predicate;

final class TaskListeners {

  private TaskListeners() {
  }

  static TaskListener onFinished(Predicate<TaskEvent> predicate, Consumer<TaskEvent> action) {
    return new TaskListener() {
      @Override
      public void finished(TaskEvent event) {
        if (predicate.test(event)) {
          action.accept(event);
        }
      }
    };
  }

  static TaskListener onFinished(Predicate<TaskEvent> predicate, Runnable action) {
    return new TaskListener() {
      @Override
      public void finished(TaskEvent event) {
        if (predicate.test(event)) {
          action.run();
        }
      }
    };
  }

  static Predicate<TaskEvent> whenEventKindIs(TaskEvent.Kind kind) {
    return event -> kind.equals(event.getKind());
  }

  static Predicate<TaskEvent> whenPackageName(Predicate<PackageName> predicate) {
    return event -> predicate.test(new PackageName(event.getCompilationUnit()));
  }
}
