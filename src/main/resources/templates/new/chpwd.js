Vue.createApp({
  data() {
    return {
      password: '',
      repassword: '',
      errorMsg: errorMsg
    }
  },
  methods: {
    submitChangePassword(e) {
      if (this.password !== this.repassword) {
        this.errorMsg = '两次密码不一致'
      } else {
        return true
      }

      e.preventDefault()
    }
  }
}).mount('#main')
