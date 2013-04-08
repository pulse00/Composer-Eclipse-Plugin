Composer Eclipse Plugin
=======================

[![Build Status](https://secure.travis-ci.org/pulse00/Composer-Eclipse-Plugin.png)](http://travis-ci.org/pulse00/Composer-Eclipse-Plugin)

Current Features
================

* [psr-0](https://github.com/php-fig/fig-standards/blob/master/accepted/PSR-0.md) autoloader support
* "New Composer project" wizard
* Install composer support into existing PHP projects
* Search and install packages on packagist.org
* Visualize your dependency graph
* Run Install/Update commands from within eclipse
* Automatically manage the buildpath for your composer dependencies. Reduces indexing time as only the required files are parsed and indexed.

INSTALLATION
============

#### Updatesite

You can choose between the stable and the development build. Stable version is released in sync with [PDT](http://www.eclipse.org/projects/project.php?id=tools.pdt) (next scheduled release: 3.2.x / June 2013).

The development updatesite can be considered production-ready, as we only deploy it when all tests pass. We'Re using the development version ourself for production projects, so you can be quite
certain that no major bugs make it into the development updatesite ;)

If you want to use the development build, make sure to also add the __PDT nightly updatesite:__ [http://download.eclipse.org/tools/pdt/updates/3.2-nightly](http://download.eclipse.org/tools/pdt/updates/3.2-nightly)!

More info about the development build can be found on [http://p2-dev.pdt-extensions.org/](http://p2-dev.pdt-extensions.org/).


### Stable Updatesite

[http://p2.dubture.com](http://p2.dubture.com)


### Development build

The development build is provided by the [PDT Extension group](https://github.com/pdt-eg) mirror:

[http://p2-dev.pdt-extensions.org/](http://p2-dev.pdt-extensions.org/)

You'll find the plugin under the `Toolchains` category.


Usage
=====

The documentation is describing the plugins latest features, only available in the development-build repository.

### Prerequisites

* You need to setup a working PHP executable in the PHP preference page "PHP executables" so `composer.phar` can be executed properly.
* To enable composer features on a an existing project, right-click the project -> `Configure` -> `Add composer support`


To see the plugin in action, checkout this [screencast](https://vimeo.com/49147387)


Contributors
============

- [Thomas Gossmann](https://github.com/gossi)
