import Vue from "vue";
import VueRouter from "vue-router";
import Login from "../views/user/Login.vue";
import Signup from "../views/user/Signup.vue";
import OauthSignup from "../views/user/OauthSignup.vue";
import Profile from "../views/user/Profile.vue";
import ConfirmEmail from "../views/user/ConfirmEmail.vue";
import UpdateInfo from "../views/user/UpdateInfo.vue";
import ArticleDetail from "../views/article/ArticleDetail.vue";
import PortfolioDetail from "../views/portfolio/PortfolioDetail.vue";
import CorporationDetail from "../views/CorporationDetail.vue";
import Main from "../views/Main.vue";
import Explore from "../views/Explore.vue";
import SearchResult from "../views/SearchResult.vue";
import NotFound from "../components/layout/NotFound.vue";

Vue.use(VueRouter);

const routes = [
  { 
    path: '*',
    name: "notfound",
    component: NotFound,
  },
  {
    path: "/",
    name: "Login",
    component: Login,
  },
  {
    path: "/signup",
    name: "signup",
    component: Signup,
  },
  {
    path: "/oauth-signup",
    name: "oauthSignup",
    component: OauthSignup,
  },
  {
    path: "/main",
    name: "main",
    component: Main,
  },
  {
    path: "/explore",
    name: "explore",
    component: Explore,
  },
  {
    path: "/user/update",
    name: "updateInfo",
    component: UpdateInfo,
  },
  {
    path: "/corporation/:companyName",
    name: "corporationDetail",
    component: CorporationDetail,
    props: true,
  },
  {
    path: "/user/:nickname",
    name: "profile",
    component: Profile,
    props: true,
  },
  {
    path: "/user/confirm-email/:useridx/code/:code",
    name: "confirmEmail",
    component: ConfirmEmail,
  },
  {
    path: "/search/:keyword",
    name: "search",
    component: SearchResult,
  },
  {
    path: "/article/:userIdx/:boardIdx",
    name: "articleDetail",
    component: ArticleDetail,
    props: true,
  },
  {
    path: "/portfolio/:userIdx/:portfolioIdx",
    name: "portfolioDetail",
    component: PortfolioDetail,
    props: true,
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

export default router;
