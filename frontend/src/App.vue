<template>
  <v-app>
    <!-- navbar는 jwt token 여부에 따라 토글한다. -->
    <!-- <Navbar/> -->
    <Navbar v-if="$store.state.config.Authorization"/>
    <v-main>
      <!-- router params 변화에 따른 동적 변화 -->
      <router-view :key="$route.fullPath"/>
      <CreateArticle v-if="$store.state.drawCreateArticle"/>
      <CreatePortfolio v-if="$store.state.drawCreatePortfolio"/>
    </v-main>
    <Footer/>
  </v-app>
</template>

<script>
import Navbar from './components/layout/Navbar.vue'
import Footer from './components/layout/Footer.vue'
import CreateArticle from '@/views/article/CreateArticle'
import CreatePortfolio from '@/views/portfolio/CreatePortfolio'

export default {
  name: 'App',
  data: () => ({
    //
  }),
  components: {
    Navbar,
    Footer,
    CreateArticle,
    CreatePortfolio,
  },
  created: function () {
    const userData = JSON.parse(localStorage.getItem('userData'))
    if (this.$route.name !== 'confirmEmail' && this.$route.name !== 'oauthSignup') {
      userData ? this.$store.commit('LOGIN', userData) : this.$router.push({ name: 'Login' })
    } 
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Fredoka+One&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Gothic+A1:wght@800;900&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Otomanopee+One&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Nanum+Gothic:wght@400;700;800&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;700;900&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Black+Han+Sans&display=swap');

.swal2-confirm {
  padding: 5px 20px 7px !important;
}

.v-pagination li button {
  width: 20px;
}

</style>