Composer Eclipse Plugin
=======================

[![Build Status](https://secure.travis-ci.org/pulse00/Composer-Eclipse-Plugin.png)](http://travis-ci.org/pulse00/Composer-Eclipse-Plugin)

Current Features
================

* [psr-0](https://github.com/php-fig/fig-standards/blob/master/accepted/PSR-0.md) autoloader support
* Install composer support into existing PHP projects
* Search and install packages on packagist.org
* Visualize your dependency graph
* Run Install/Update commands from within eclipse

INSTALLATION
============

#### Updatesite

You can choose between the stable and the nightly build. Stable version is released in sync with [PDT](http://www.eclipse.org/projects/project.php?id=tools.pdt) (next scheduled release: 3.1.2 / 22 Feb 13).

The nightly updatesite is build once a day automatically and published if all tests succeed. I'm using the nightly version myself for my production projects, so you can be quite
certain that no major bugs make it into the nightly updatesite ;)

If you want to use the nightly build, make sure to also add the __PDT nightly updatesite:__ [http://download.eclipse.org/tools/pdt/updates/nightly](http://download.eclipse.org/tools/pdt/updates/nightly)!


### Stable Updatesite

[http://p2.dubture.com](http://p2.dubture.com)


### Nightly build

The nightly build is provided by the [PDT Extension group](https://github.com/pdt-eg) mirror:

[http://p2-dev.pdt-extensions.org/](http://p2-dev.pdt-extensions.org/)


Usage
=====

The documentation is describing the plugins latest features, only available in the nightly-build repository. To read the documentation for the 
stable release, see https://github.com/pulse00/Composer-Eclipse-Plugin/tree/stable.

### Prerequisites

* You need to setup a working PHP executable in the PHP preference page "PHP executables" so `composer.phar` can be executed properly.
* If you want to use buildpath management (can increase PDT performance), enable it under eclipse preferences -> PHP -> Composer -> Enable buildpath management
* To enable composer features on a project, right-click the project preferences and activate the `Composer Facet` under the `Project Facets` tab


This plugin is still beta.


To see the plugin in action, checkout this [screencast](https://vimeo.com/49147387)

