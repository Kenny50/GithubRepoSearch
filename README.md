# GithubRepoSearch

GithubRepoSearch is a sample project build with Material Design components in xml, the goal of this
project is to show to architecture of an Application.

To run this project with android studio, you could use git clone
from [here](https://github.com/Kenny50/GithubRepoSearch), and please add following content into
local.properties.

```text
test.base.url="https://api.github.com/"
test.client.token="your_github_token"

release.base.url="https://api.github.com/"
release.client.token="your_github_token"
```

To create github token, please check their
document [here](https://docs.github.com/cn/developers/apps/building-oauth-apps/creating-an-oauth-app)
.

## Screen shots

<img src="screenshots/github_repo_search_demo.gif" alt="Screenshot">

## Features

This project contain two screen, search screen and setting screen. You can set the auto search
enable/ disable from setting screen.

## presentation

package [`com.kenny.githubreposearch.presentation`][1]
The Ui layer of this project.

[1]: app/src/main/java/com/kenny/githubreposearch/presentation

### Home

package [`com.kenny.githubreposearch.presentation.home`][2]
Start page of this app,
using [paging3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) to
create infinite scroll list,
also [determine load state](https://developer.android.com/topic/libraries/architecture/paging/load-state#additional-info)
to display status Ui elements.

- using pagingAdapter and paging3.
- using coroutine for auto search.
- read setting
  from [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) to change
  auto search enable/ disable.

[2]: app/src/main/java/com/kenny/githubreposearch/presentation/home

### Setting

package [`com.kenny.githubreposearch.presentation.setting`][3]
Setting page to change auto search function.

- write setting to [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
  to change auto search enable/ disable.

[3]: app/src/main/java/com/kenny/githubreposearch/presentation/setting

### RepoLoadState

package [`com.kenny.githubreposearch.presentation.repo_load_state`][4]
Header/ Footer for pagingAdapter display loading, error status.

[4]: app/src/main/java/com/kenny/githubreposearch/presentation

## data

package [`com.kenny.githubreposearch.data`][5]
Process data before UseCase, include Dto, Vo, PagingSource, RepositoryImpl and retrofit service
interface.

[5]: app/src/main/java/com/kenny/githubreposearch/data

## di

package [`com.kenny.githubreposearch.di`][6]
Using [Hilt](https://developer.android.com/training/dependency-injection) as dependency injection
package to simplify code.

[6]: app/src/main/java/com/kenny/githubreposearch/data

## domain

package [`com.kenny.githubreposearch.domain`][7]
MVVM repository interface and useCase layer, connect with viewModel and model layer.

[7]: app/src/main/java/com/kenny/githubreposearch/domain

## Unit Test

### pagingSource

Test paging source work as except, with three different test

- first call, should return 100 counts data
- normal call, should return 50 counts data
- final call, should return 45 counts data

### repository

Repository test, inheritance GithubRepository, testing `Dto to Vo` is correct.