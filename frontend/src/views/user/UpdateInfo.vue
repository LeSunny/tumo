<template>
  <div class="container d-flex justify-center my-5 h-100">
    <div id="userUpdate" class="d-flex flex-column align-items-center h-100 fw-bold">
      <h1 class="my-5">회원정보 변경</h1>
      <v-form
        ref="form"
        v-model="valid"
        id="userUpdateForm"
      >
        <!-- 서비스 내 이름 -->
        <div id="nickNameInput" class="position-relative">
          <v-text-field
            v-model="credentials.name"
            :rules="nameRules"
            :counter="10"
            label="Name"
            required
            @input="name_checked = false"
          >
          </v-text-field>
          <v-btn
          :disabled="!name_exist"
          small
          @click="name_check"
          >중복검사</v-btn>
          <v-icon v-if="name_checked" color="success" class="checkBtn">mdi-check-bold</v-icon>
        </div>

        <!-- 이메일 계정 -->
        <div id="emailInput" class="position-relative">
          <v-text-field
            :value="credentials.email"
            label="E-mail"
            disabled
          ></v-text-field>
          <v-switch
            v-model="credentials.disclosure"
            inset
            hide-details
            class="my-auto"
            id="toggleBtn"
            :label="credentials.disclosure ? 'Public' : 'Private'"
          ></v-switch>
        </div>

        <!-- 자기소개 -->
        <v-text-field
          :value="credentials.introduce"
          v-model="credentials.introduce"
          label="Introduce"
        ></v-text-field>

        <!-- 관심 키워드 입력 -->
        <div class="d-flex align-items-center w-100">
          <v-text-field
            v-model="keyword"
            label="Hash Tag"
            @keypress.enter="add_keyword"
          ></v-text-field>
          <v-btn color="primary" class="ms-5" @click="add_keyword">추가</v-btn>
        </div>
        
        <!-- 선택된 키워드 or default -->
        <div id="keyword_items">
          <v-chip 
          v-for="(keyword, idx) in credentials.keywords" 
          :key="idx"
          class="me-2"
          color="#00BFFE"
          text-color="white"
          close
          @click:close="delete_keyword(keyword)"
          >
          {{ keyword }}
          </v-chip>
        </div>

        <div id="btnGroup" class="my-3">
          <div>
            <v-btn
              id="cancel_btn"
              class="mr-4 "
              @click="cancel"
            >
              cancel
            </v-btn>
            <v-btn 
              id="signup_btn"    
              @click="update"
              :disabled="!valid || !name_checked"
            >
              update
            </v-btn>
          </div>
        </div>
      </v-form>
      <p class="my-3 text-primary" style="cursor: pointer;" @click="drawUpdatePassword" v-if="this.$store.state.user_info.oauth !== 'google'">비밀번호를 변경하시겠어요?</p>
      <UpdatePassword v-if="$store.state.drawUpdatePassword"/>
      <p class="my-3 text-danger" style="cursor: pointer;" @click="drawDeleteAccount">계정을 삭제 할건가요.....?</p>
      <DeleteAccount v-if="$store.state.drawDeleteAccount"/>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import UpdatePassword from '@/components/account/UpdatePassword'
import DeleteAccount from '@/components/account/DeleteAccount'

export default {
  name: 'UpdateInfo',
  components: {
    UpdatePassword,
    DeleteAccount
  },    
  data: () => {
    return {
      // 요구사항 만족
      valid: true,

      // 서버와 통신할 데이터
      credentials: {
        name: "",
        introduce: "",
        email: "",
        disclosure: true,
        keywords: [],
      },
      keyword: "",

      // 중복검사 여부
      name_exist: false,
      name_checked: true,
    }
  },
  // 닉네임 및 이메일 중복 검사 하기 전 조건에 맞는 입력값인지 확인
  watch: {
    credentials: {
      handler: function () {
        if (this.credentials.name && this.credentials.name.length <= 10) {
          // 이미 기존 닉네임과 동일한 경우 중복검사 버튼 비활성화 및 name_check = true
          if (this.credentials.name === this.$store.state.user_info.nickname) {
            this.name_exist = false
            this.name_checked = true
          } else {
            this.name_exist = true
          }
        } else {
          this.name_exist = false
        }
      },
      deep: true
    }
  },
  methods: {
    // 닉네임 중복검사 axios 요청 보낼 것.
    name_check: function () {
      if (this.credentials.name === this.$store.state.user_info.nickname) {
        this.name_checked = true
      } else {
        axios({
          method: 'GET',
          url: `/api/user/nickname/${this.credentials.name}`
        })
        .then(res => {
          const message = res.data.message
          if (message === "success") {
            this.name_checked = true
            this.name_exist = false
            console.log('ok')
          } else {
            alert('이미 존재하는 닉네임입니다.')
            this.name_checked = false
          }
        })
        .catch(err => {
          console.log(err)
        })
      }
    },
    // 취소 -> 로그인 페이지로
    cancel: function () {
      this.$router.push({ name: 'main' })
    },
    // update axios 요청 보내기
    update: function () {
      // request에 필요한 data
      const data = {
        userIdx: this.$store.state.user_info.id,
        nickname: this.credentials.name,
        disclosure: (this.credentials.disclosure) ? 'public' : 'private',
        introduce: this.credentials.introduce,
        tags: this.credentials.keywords,
        oauth: this.$store.state.user_info.oauth,
      }
      // axios 요청
      axios({
        method: 'PUT',
        url: '/api/user/update',
        data: data
      })
      .then(res => {
        const message = res.data.message
        if (message === "success") {
          let userData = JSON.parse(localStorage.getItem('userData'))
          userData = {...userData, ...data, tags: [...data.tags]}
          localStorage.setItem('userData', JSON.stringify(userData))
          this.$store.commit('INFO_UPDATE', data)
          this.$alert("성공적으로 정보가 수정되었습니다.", "수정 완료", 'success')
          .then(() => {
            this.$router.push({ name: 'main' })
          })
        }
      })
    },
    // keyword 추가하기
    add_keyword: function () {
      //serve로 보낼 user data에 추가
      if (this.keyword.trim()) {
        this.credentials.keywords.push(this.keyword)
      }
      this.keyword = ""
    },
    // keyword 제거
    delete_keyword: function (keyword) {
      // console.log(keyword)
      const idx_keyword = this.credentials.keywords.indexOf(keyword)
      this.credentials.keywords.splice(idx_keyword, 1)
    },
    drawUpdatePassword: function () {
      this.$store.state.drawUpdatePassword = true
    },
    drawDeleteAccount: function () {
      this.$store.state.drawDeleteAccount = true
    }
  },
  computed: {
    // 회원가입 규칙
    nameRules: function () {
      return [
        v => !!v || '닉네임은 필수 입력사항입니다.',
        v => (v && v.length <= 10) || '닉네임은 10자를 넘을 수 없습니다.',
      ]
    },
  },
  created: function () {
    this.credentials.name = this.$store.state.user_info.nickname
    this.credentials.email = this.$store.state.user_info.email
    this.credentials.introduce = this.$store.state.user_info.introduce
    this.credentials.keywords = this.$store.state.user_info.tags
    this.credentials.disclosure = (this.$store.state.user_info.disclosure) === 'public' ? true : false
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Jua&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR&display=swap');

#userUpdate {
  width: 600px;
}

#btnGroup {
  display: flex;
  flex-direction: row;
}


#signup_btn {
  background-color: #00BFFE;
  color: white;
}

#cancel_btn {
  background-color: #CE1D28;
  color: white;
}

.keywords_tag {
  background-color: #00BFFE;
}

a, p {
text-decoration: none;
font-family: 'Noto Sans KR', sans-serif;
}

#nickNameInput > button {
  position: absolute;
  right: 0;
  top: 20%;
}

#userUpdateForm {
  width: 80%;
}

#btnGroup {
  justify-content: center;
}

#emailInput div:last-child {
  position: absolute;
  top: 15%;
  left: 78%;
}

.checkBtn {
  position: absolute;
  left: 100%;
  top: 20%;
  padding-left: 8px;
}

@media screen and (min-width: 500px){
  #btnGroup {
    justify-content: flex-end;
    align-items: center;
  }
}

@media screen and (max-width: 599px){
  #userUpdate {
    width: 100%;
  }

  #nickNameInput > button {
    right: 0;
  }
}
</style>