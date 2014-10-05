Example from Professional Android 4

Notice:

To make it work with Eclipse:

1. Configure scala compiler's build order to `JavaThenScala`.
Otherwise it will report `R` not found error.

2. Add AndroidScalaProguard plugin. Otherwise implicit class will not work
