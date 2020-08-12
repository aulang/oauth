$(function () {
    var password = $('#password');
    var confirmed = $('#confirmedPassword');
    var patternRegex = new RegExp('^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]{6,20}$');

    $('#passwordFormSubmit').click(function () {
        var passwordVal = password.val();
        var confirmedVal = confirmed.val();
        if (!patternRegex.test(passwordVal)) {
            $('#errorMsg').html('密码格式：6-20位，不能为纯字母或数字！');
            return;
        }

        if (passwordVal != confirmedVal) {
            $('#errorMsg').html('两次密码不一致！');
            return;
        }

        /**
         * 密码进行SHA256
         */
        password.val(sha256(passwordVal));
        confirmed.val(sha256(confirmedVal));

        $('#passwordForm').submit();
    });
});