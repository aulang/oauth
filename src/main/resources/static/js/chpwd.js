$(function () {
    var password = $('#password');
    var confirmed = $('#confirmedPassword');
    var patternRegex = new RegExp('^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]{6,20}$');

    $('#passwordFormSubmit').click(function () {
        if (!patternRegex.test(password.val())) {
            $('#errorMsg').html('密码格式：6-20位，不能为纯字母或数字！');
            return;
        }

        if (password.val() != confirmed.val()) {
            $('#errorMsg').html('两次密码不一致！');
            return;
        }
        $('#passwordForm').submit();
    });
});