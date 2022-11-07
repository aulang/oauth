function changePassword(e) {
  let password = document.getElementById('password').value
  let repassword = document.getElementById('repassword').value

  if (password !== repassword) {
    document.getElementById('error-msg').innerHTML = '两次密码不一致'
  }

  e.preventDefault()
}
