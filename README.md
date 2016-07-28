xdgpaths
========
A utility for obtaining Path instances relative to the user home directory
and the cache, config, data and runtime directories specified in the
freedesktop.org XDG Base Directory Specification.

As an extension to the specification, the base directories may be specified
using java system properties using keys that match the environment variable
names defined in the specification. The system properties will be used if
set, otherwise the environment variables will be used if set, falling back
to the defaults defined in the specification if neither the system property
nor environment variable is set.

### User Home Directory

The user home directory is specified in the user.home system property if set,
otherwise the HOME environment variable.

```java

  // "/home/user"
  System.getProperty("user.home");

  // "/home/user"
  XdgPaths.getInstance().home().toString();

  // "/home/user/foo/bar"
  XdgPaths.getInstance().home("foo","bar").toString();

  // "/home/user/foo/bar"
  XdgPaths.getInstance().home("foo/bar").toString();

  // "/foo/bar" (absolute path specified)
  XdgPaths.getInstance().home("/foo/bar").toString();

```

### Cache Directory
The user specific cache directory is specified in the XDG_CACHE_HOME system
property if set, otherwise the XDG_CACHE_HOME environment variable, or
defaults to .cache relative to the user's home directory.

```java

  // "/home/user"
  System.getProperty("user.home");

  // "/home/user/.cache"
  XdgPaths.getInstance().cache().toString();

  // "/home/user/.cache/foo/bar"
  XdgPaths.getInstance().cache("foo","bar").toString();

  // "/home/user/.cache/foo/bar"
  XdgPaths.getInstance().cache("foo/bar").toString();

  // "/foo/bar" (absolute path specified)
  XdgPaths.getInstance().cache("/foo/bar").toString();

```

### User Config Directory
The user specific config directory is specified in the XDG_CONFIG_HOME system
property if set, otherwise the XDG_CONFIG_HOME environment variable, or defaults
to .config relative to the user's home directory.

```java

  // "/home/user"
  System.getProperty("user.home");

  // "/home/user/.config"
  XdgPaths.getInstance().userconfig().toString();

  // "/home/user/.config/foo/bar"
  XdgPaths.getInstance().userconfig("foo","bar").toString();

  // "/home/user/.config/foo/bar"
  XdgPaths.getInstance().userconfig("foo/bar").toString();

  // "/foo/bar" (absolute path specified)
  XdgPaths.getInstance().userconfig("/foo/bar").toString();

```

### User Data Directory
The user specific data directory is specified in the XDG_DATA_HOME system
property if set, otherwise the XDG_DATA_HOME environment variable, or defaults
to .local/share relative to the user's home directory.

```java

  // "/home/user"
  System.getProperty("user.home");

  // "/home/user/.local/share"
  XdgPaths.getInstance().userdata().toString();

  // "/home/user/.local/share/foo/bar"
  XdgPaths.getInstance().userdata("foo","bar").toString();

  // "/home/user/.local/share/foo/bar"
  XdgPaths.getInstance().userdata("foo/bar").toString();

  // "/foo/bar" (absolute path specified)
  XdgPaths.getInstance().userdata("/foo/bar").toString();

```

### User Runtime Directory
The user specific runtime directory is specified in the XDG_RUNTIME_DIR system
property if set, otherwise the XDG_RUNTIME_DIR environment variable. Unlike
the cache, config and data directories, the specification does not define a
default location for a user runtime directory. XdgPaths.runtime() will return
an Optional<Path> with a null value if the runtime directory is not explicitly
set via the system property or environment variable.

```java

  // "/var/run/user/1234"
  System.getenv("XDG_RUNTIME_DIR");

  // true
  XdgPaths.getInstance().runtime().get().isPresent();

  // "/var/run/user/1234"
  XdgPaths.getInstance().runtime().get().toString();

  // "/var/run/user/1234/foo/bar"
  XdgPaths.getInstance().runtime("foo","bar").get().toString();

  // "/var/run/user/1234/foo/bar"
  XdgPaths.getInstance().runtime("foo/bar").get().toString();

  // "/foo/bar" (absolute path specified)
  XdgPaths.getInstance().runtime("/foo/bar").toString();

```

If XDG_RUNTIME_DIR is not set:

```java

  // null
  System.getProperty("XDG_RUNTIME_DIR");

  // null
  System.getenv("XDG_RUNTIME_DIR");

  // false
  XdgPaths.getInstance().runtime().get().isPresent();

  // null
  XdgPaths.getInstance().runtime().get();

  // false
  XdgPaths.getInstance().runtime("foo","bar").isPresent();

  // null
  XdgPaths.getInstance().runtime("foo","bar").get();

  // false (absolute path not resolved if runtime is unset)
  XdgPaths.getInstance().runtime("/foo/bar").isPresent();

  // null (absolute path not resolved if runtime is unset)
  XdgPaths.getInstance().runtime("/foo/bar").get();

```

### Config Directories
In addition to the user specific config directory, additional config directories
may be specified using the XDG_CONFIG_DIRS system property or environment
variable. If neither is set, the default will be /etc/xdg. The config() method
returns a list of paths relative to each of the config directories in the
order of precedence defined in the specification, with the user specific
config directory first, followed by each additional config directory in the
order they are specified:

```java

  // "/home/user"
  System.getProperty("user.home");

  // 2
  XdgPaths.getInstance().config().size();

  // "/home/user/.config"
  XdgPaths.getInstance().config().get(0).toString();

  // "/etc/xdg"
  XdgPaths.getInstance().config().get(1).toString();

  // 2
  XdgPaths.getInstance().config("foo", "bar").size();

  // "/home/user/.config/foo/bar"
  XdgPaths.getInstance().config("foo","bar").get(0).toString();

  // "/etc/xdg/foo/bar"
  XdgPaths.getInstance().config("foo","bar").get(1).toString();

  // 1 (absolute path returns single element list)
  XdgPaths.getInstance().config("/foo/bar").size();

  // "/foo/bar" (absolute path returns single element list)
  XdgPaths.getInstance().config("/foo/bar").get(0).toString();

```

### Data Directories
In addition to the user specific data directory, additional data directories
may be specified using the XDG_DATA_DIRS system property or environment
variable. If neither is set, the default will be /usr/local/share:/usr/share.
The data() method returns a list of paths relative to each of the data
directories in the order of precedence defined in the specification, with the
user specific data directory first, followed by each additional data directory
in the order they are specified:

```java

  // "/home/user"
  System.getProperty("user.home");

  // 3
  XdgPaths.getInstance().data().size();

  // "/home/user/.local/share"
  XdgPaths.getInstance().data().get(0).toString();

  // "/usr/local/share"
  XdgPaths.getInstance().data().get(1).toString();

  // "/usr/share"
  XdgPaths.getInstance().data().get(2).toString();

  // 1 (absolute path returns single element list)
  XdgPaths.getInstance().data("/foo/bar").size();

  // "/foo/bar" (absolute path returns single element list)
  XdgPaths.getInstance().data("/foo/bar").get(0).toString();

```

### Get Path With Token Replacement
The get method will return a list of paths, replacing a token prefix with the
appropriate base directories. The token must be specified in the first string
parameter:

```java

  // "/home/user"
  System.getProperty("user.home");

  // 1
  XdgPaths.getInstance().get("%home/foo").size();

  // "/home/user/foo"
  XdgPaths.getInstance().get("%home/foo").get(0);

  // 1
  XdgPaths.getInstance().get("%cache/foo").size();

  // "/home/user/.cache/foo"
  XdgPaths.getInstance().get("%cache/foo").get(0);

  // 1
  XdgPaths.getInstance().get("%userconfig/foo").size();

  // "/home/user/.config/foo"
  XdgPaths.getInstance().get("%userconfig/foo").get(0);

  // 1
  XdgPaths.getInstance().get("%userdata", "foo").size();

  // "/home/user/.local/share/foo"
  XdgPaths.getInstance().get("%userdata", "foo").get(0);

  // 0 (%runtime returns zero element list if runtime dir not set)
  XdgPaths.getInstance().get("%runtime", "foo").size();

  // 2
  XdgPaths.getInstance().get("%config", "foo", "bar").size();

  // "/home/user/.config/foo/bar"
  XdgPaths.getInstance().get("%config", "foo","bar").get(0).toString();

  // "/etc/xdg/foo/bar"
  XdgPaths.getInstance().get("%config", "foo","bar").get(1).toString();

  // 3
  XdgPaths.getInstance().get("%data", "foo", "bar").size();

  // "/home/user/.local/share/foo/bar"
  XdgPaths.getInstance().get("%data", "foo","bar").get(0).toString();

  // "/usr/local/share/foo/bar"
  XdgPaths.getInstance().get("%data", "foo","bar").get(1).toString();

  // "/usr/share/foo/bar"
  XdgPaths.getInstance().get("%data", "foo","bar").get(2).toString();

```

If a token is not present, a single element list will be returned with the
single path:

```java

  // 1
  XdgPaths.getInstance().get("foo/bar").size();

  // "foo/bar" (relative path)
  XdgPaths.getInstance().get("foo/bar").get(0).toString();

  // 1
  XdgPaths.getInstance().get("/foo/bar").size();

  // "/foo/bar" (absolute path)
  XdgPaths.getInstance().get("/foo/bar").get(0).toString();

```

### License
Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
