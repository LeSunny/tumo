<template>
  <v-sheet 
    elevation="4"
    rounded
    @click="moveToDetail"
    class="portfolio-card"
  >
    <div class="d-flex justify-content-between align-items-center">
      <div class="d-flex align-items-center">
        <img src="@/assets/main/user.png" alt="user_img" style="width: 30px; margin: 0;">
        <div class="d-flex align-items-center">
          <h6 class="mb-0 mx-3 fw-bold">{{ feed.title }}</h6>
          <router-link 
            class="text-secondary nickname" 
            :to="{ name: 'profile', params: { nickname: `${feed.nickname}` }}"
            style="font-size: 1rem;"
          >
            @{{ feed.nickname }}
          </router-link>
        </div>
      </div>
    </div>

    <div class="chartContent">
      <canvas :id="`myChart${idx}`" width="150" height="150"></canvas>
      <div v-if="myChart" class="d-grid chart w-100">
        <div class="row">
          <div class="col">
            <p style="font-size: 1.6rem; font-weight: 700;">전체 수익률</p>
            <p>목표 수익률</p>
            <p>전체 투자액</p>
            <p>총 평가액</p>
          </div>
          <div class="col" align="right">
            <p style="font-size: 1.6rem; font-weight: 700;"><span :class="{ 'text-danger': feed.percent > 0, 'text-primary': feed.percent < 0 }">{{ feed.percent }}</span> %</p>
            <p><span :class="{ 'text-danger': feed.goal > 0, 'text-primary': feed.goal < 0 }">{{ feed.goal }}</span> %</p>
            <p>{{ feed.sum | money }}원</p>
            <p>{{ feed.cursum | money }}원</p>
          </div>
        </div>
        <div class="row mt-3 assets fw-bold">
          <p class="col-4">주요 개별자산</p>
          <p class="col-5" align="right">매수 가격</p>
          <p class="col" align="right">수익률</p>
        </div>
        <div v-for="(asset, idx) in topAssets" :key="idx" class="row assets">
          <p class="col-4">{{ asset.name }}</p>
          <p class="col-5" align="right">{{ asset.price | money }} 원</p>
          <p class="col" align="right"><span :class="{ 'text-danger': asset.percent > 0, 'text-primary': asset.percent < 0 }">{{ asset.percent | money }}</span> %</p>
        </div>
      </div>
    </div>
  </v-sheet>
</template>

<script>
import axios from 'axios'
import Chart from 'chart.js/auto'

export default {
  name: 'PortfolioCard',
  props: {
    feed: {
      type: Object
    },
    idx: {
      type: Number
    } 
  },
  data: function () {
    return {
      myChart: false,
    }
  },
  methods: {
    moveToDetail: function () {
      this.$router.push({ name: 'portfolioDetail', params: { userIdx: `${this.feed.user_idx}`, portfolioIdx: this.feed.portfolio_idx }})
    },
    getChart: function () {
      axios({
        method: 'GET',
        url: `/api/portfolio/asset/${this.feed.portfolio_idx}`
      })
      .then(res => {
        const defaultData = {
          amount: {
            sum: 0,
            cursum: 0,
            percent: 0
          },
          Asset: []
        }
        const data = res.data || defaultData      
        Object.assign(this.$data, data)

        data.Asset.sort((a, b) => { return b.curprice - a.curprice })
        this.topAssets = [...data.Asset].sort((a, b) => { return b.percent - a.percent }).slice(0, 3)
        this.topAssets = data.Asset.slice(0, 3)
        const stockData = data.Asset.map(data => {
          return data.name
        })
        const priceData = data.Asset.map(data => {
          return data.curprice
        })
        return { stockData, priceData }
      })
      .then(res => {
        const ctx = document.getElementById(`myChart${this.idx}`)
        const myChart = new Chart(ctx, {
          type: 'pie',
          data: {
            labels: res.stockData,
            datasets: [{
              label: 'portfolio',
              data: res.priceData,
              backgroundColor: [
                '#00BFFE',
                '#57648C',
                '#C2B4D6',
                '#ABDFF1',
                '#E1DDDB',
                '#FFFEE6',
              ],
              hoverOffset: 4
            }]
          },
          options: {
            plugins: {
              legend: {
                display: false,
                },
              },
            responsive: false,
          }
        })
        this.myChart = true
        console.log(myChart)
      })
    }
  },
  mounted: function () {
    this.getChart()
  }
}
</script>

<style>
.portfolio-card {
  width: 100%;
  margin: 10px 0px;
  padding: 1rem 0rem;
  transition: 0.5s;
  cursor: pointer;
  font-family: 'Noto Sans KR', sans-serif;
}

.portfolio-card > * {
  margin: 0% 7%;
}

.portfolio-card > div:first-child {
  margin: 0rem 1rem;
}


.portfolio-card .chart p {
  margin-bottom: 0.2rem;
}

.portfolio-card .chart .assets p {
  font-size: 0.75rem;
}

.portfolio-card .chartContent {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.portfolio-card .chartContent canvas {
  margin: 1.5rem 0rem;
}
</style>