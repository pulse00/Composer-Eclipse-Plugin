Buildpath management
==================== 

The plugin is capable of managing your composer dependencies as custom PDT buildpath entries. Composer maintains 2 important files in `vendor/composer` which
describe the complete setup of your dependencies:

- `installed.json`
- `installed_dev.json` 

With the `buildpath management` feature enabled (see eclipse preferences -> PHP -> Composer to enable it), PDT will watch these files for changes and if they do
change, it will do the following:

* For each vendor/package/version combination, it will create a locally cached copy of the package under `${workspaceLocation}/.metadata/.plugins/com.dubture.composer/packages`
* The `vendor` folder is removed from your projects buildpath
* Instead of parsing and indexing your dependencies for each project separately, PDT will be able to index common dependencies across projects using the local cache.

This can increase the indexing process dramatically, as your buildpath only contains your projects sources, meaning for each project indexing and validation will only happen
on the sources of your project, not on your dependencies.

Please not that this feature will not modify the way composer works in any way, your vendor directory still contains all the latest references from `composer.lock` and any 
composer operation is still performed directly via executing `composer.phar`. 

It simply means that common libraries will maintain a common PDT index, reducing the amount of time eclipse is indexing the vendor directory.


