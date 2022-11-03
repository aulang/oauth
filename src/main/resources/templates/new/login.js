Vue.createApp({
  data() {
    return {
      username: '',
      rememberMe: false,
      errorMsg: errorMsg,
      securityCodeLogin: false,
      disableSendSecurityCode: false,
      sendSecurityCodeSuccess: false,
      sendSecurityCodeMsg: '发送验证码'
    }
  },
  mounted() {
    if (localStorage.rememberMe) {
      this.rememberMe = localStorage.rememberMe
    }
    if (localStorage.username) {
      this.username = localStorage.username
    }
  },
  methods: {
    changeLoginType() {
      this.securityCodeLogin = !this.securityCodeLogin
      this.errorMsg = ''
    },
    sendSecurityCode() {
      let mobilephone = document.getElementById('mobilephone')
      if (!mobilephone.checkValidity()) {
        mobilephone.reportValidity()
        return
      }

      let thiz = this
      let counter = 60
      thiz.errorMsg = ''
      thiz.disableSendSecurityCode = true
      thiz.sendSecurityCodeSuccess = true
      let timer = setInterval(() => {
        --counter

        thiz.sendSecurityCodeMsg = `${counter}s`

        if (counter == 0) {
          thiz.sendSecurityCodeMsg = '发送验证码'
          thiz.disableSendSecurityCode = false
          clearInterval(timer)
        }
      }, 1000)
    },
    submitPasswordLogin(e) {
      if (this.rememberMe) {
        localStorage.rememberMe = this.rememberMe
        localStorage.username = this.username
      } else {
        localStorage.removeItem('rememberMe')
        localStorage.removeItem('username')
      }

      e.preventDefault()
    },
    summitSecurityCodeLogin(e) {
      if (!this.sendSecurityCodeSuccess) {
        this.errorMsg = '请先发送验证码'
      }

      e.preventDefault()
    }
  }
}).mount('#main')
