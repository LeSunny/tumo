<template>
  <div id="mainfeed">
    <!-- Tab Grop -->
    <v-tabs grow :color="tabColor" id="feedTabs">
      <v-tab id="newFeedBtn" @click="selectNewFeeds">
        <v-icon large class="me-3">mdi-account-group</v-icon>
        <span>Feeds</span>
      </v-tab>
      <v-tab id="portfolioBtn" @click="selectPortfolio">
        <v-icon large class="me-3">mdi-chart-bar</v-icon>
        <span>Portfolio</span>
      </v-tab>
    </v-tabs>
    <!-- newFeed -->
    <div v-if="selectedTab === 'newfeeds'">
      <ArticleFeed v-for="(feed, idx) in articleFeedList" :key="idx" :feed="feed" />
    </div>
    <!-- Portfolio -->
    <div v-else-if="selectedTab === 'portfolio'">
      <PortfolioFeed v-for="(feed, idx) in portfolioFeedList" :key="idx" :feed="feed" :idx="idx"/>
    </div>
    <infinite-loading v-if="changeTab" @infinite="feedInfiniteHandler" spinner="waveDots" class="mt-5">
      <div slot="no-more" style="color: rgb(102, 102, 102); font-size: 14px; padding: 10px 0px;">목록의 끝입니다 :)</div>
    </infinite-loading>
  </div>
</template>

<script>
import axios from 'axios'
import ArticleFeed from './ArticleFeed'
import PortfolioFeed from './PortfolioFeed'
import InfiniteLoading from 'vue-infinite-loading'

export default {
  name: 'FeedList',
  data: function () {
    return {
      selectedTab: 'newfeeds',
      tabColor: 'primary',
      articleFeedList: [],
      portfolioFeedList: [],
      pageNum: 0,
      changeTab: true
    }
  },
  components: {
    ArticleFeed,
    PortfolioFeed,
    InfiniteLoading,
  },
  methods: {
    selectNewFeeds: function () {
      if (this.selectedTab !== 'newfeeds') {
        this.changeTab = false
        this.pageNum = 0
        this.articleFeedList = []
        this.selectedTab = 'newfeeds'
        this.tabColor = 'primary'
        setTimeout(() => {
          this.changeTab = true
        }, 100)
      }
    },
    selectPortfolio: function () {
      if (this.selectedTab !== 'portfolio') {
        this.changeTab = false
        this.pageNum = 0
        this.portfolioFeedList = []
        this.tabColor = 'error'
        this.selectedTab = 'portfolio'
        setTimeout(() => {
          this.changeTab = true
        }, 100)
      }
    },
    getNewFeeds: function () {
      return axios({
        method: 'GET',
        url: `/api/feed/${this.$store.state.user_info.id}/${this.pageNum}`
      })
      .then(res => {
        const feedList = res.data.feedList
        return feedList ?? []
      })
    },
    getPortfolioFeeds: function () {
      return axios({
        method: 'GET',
        url: `/api/portfolio/feedlist/${this.$store.state.user_info.id}/${this.pageNum}`
      })
      .then(res => {
        const feedList = res.data.portfolio
        return feedList ?? []
      })
    },
    feedInfiniteHandler: function ($state) {
      if (this.selectedTab === 'newfeeds') {
        const EACH_LEN = 10
        this.getNewFeeds()
        .then(data => {
          setTimeout(() => {
            if (data.length) {
              this.articleFeedList = this.articleFeedList.concat(data)
              $state.loaded()
              this.pageNum += 1
              if (data.length / EACH_LEN < 1) {
                $state.complete()
              }
            } else {
              $state.complete()
            }
          }, 500)
        })
      } else {
        const EACH_LEN = 5
        this.getPortfolioFeeds()
        .then(data => {
          setTimeout(() => {
            if (data.length) {
              this.portfolioFeedList = this.portfolioFeedList.concat(data)
              $state.loaded()
              this.pageNum += 1
              if (data.length / EACH_LEN < 1) {
                $state.complete()
              }
            } else {
              $state.complete()
            }
          }, 500)
        })
      }
    },
  },
}
</script>

<style>
#mainfeed {
  width: 95%;
}

#feedTabs {
  position: sticky;
  padding-top: 3rem;
  top: 48px;
  background-color: white;
  z-index: 1;
}

#feedTabs span {
  font-family: 'Otomanopee One', sans-serif;
}

@media screen and (min-width: 576px) {
  #mainfeed {
    width: 614px;
    padding: 0 1.5rem;
  } 
}

@media screen and (min-width: 940px) {
  #mainfeed {
    padding: 0 3rem 0 0;
  }
}


</style>