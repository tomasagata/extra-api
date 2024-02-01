package org.mojodojocasahouse.extra.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.mojodojocasahouse.extra.dto.requests.*;
import org.mojodojocasahouse.extra.dto.responses.ApiResponse;
import org.mojodojocasahouse.extra.exception.EmailException;
import org.mojodojocasahouse.extra.exception.ExistingUserEmailException;
import org.mojodojocasahouse.extra.exception.InvalidPasswordResetTokenException;
import org.mojodojocasahouse.extra.model.*;
import org.mojodojocasahouse.extra.repository.ExtraUserRepository;
import org.mojodojocasahouse.extra.repository.PasswordResetTokenRepository;
import org.mojodojocasahouse.extra.repository.UserDeviceRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender mailSender;

    private final ExtraUserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserDeviceRepository deviceRepository;


    public ApiResponse registerUser(UserRegistrationRequest userRegistrationRequest)
        throws ExistingUserEmailException {

        // validate email
        validateEmailUniqueness(userRegistrationRequest);

        // Create encoded password
        String encodedPassword = passwordEncoder.encode(userRegistrationRequest.getPassword());

        // create user entity from request data
        ExtraUser newUser = ExtraUser.from(userRegistrationRequest, encodedPassword);

        // Save new user
        userRepository.save(newUser);

        return new ApiResponse("User created successfully");
    }

    public ExtraUser getUserByPrincipal(Principal principal) throws RuntimeException{
        return userRepository.findByEmail(principal.getName()).orElseThrow(RuntimeException::new);
    }


    private void validateEmailUniqueness(UserRegistrationRequest userRequest) throws ExistingUserEmailException{
        userRepository
                .findByEmail(userRequest.getEmail())
                .ifPresent(s -> {throw new ExistingUserEmailException();});
    }

    public ApiResponse changePassword(ExtraUser user, @Valid UserChangePasswordRequest userChangePasswordRequest) {
        // Function that given the correct old password, sets a new one for the given user
        // if the old password is incorrect, it returns an error
        String newPassword = passwordEncoder.encode(userChangePasswordRequest.getNewPassword());

        if(!passwordEncoder.matches(userChangePasswordRequest.getCurrentPassword(), user.getPassword())){
            throw new BadCredentialsException("Bad credentials");
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        return new ApiResponse("Password changed successfully");
    }

    public ApiResponse sendPasswordResetEmail(ForgotPasswordRequest request) throws EmailException {
        Optional<ExtraUser> foundUser = userRepository.findByEmail(request.getEmail());

        if(foundUser.isPresent()){
            log.debug("SendPasswordResetEmail: User \"" + request.getEmail() + "\" found.");

            ExtraUser user = foundUser.get();
            PasswordResetToken token = tokenRepository.save(
                    new PasswordResetToken(user)
            );

            try {
                sendEmail(user, token);
            } catch (MailException | MessagingException ex) {
                throw new EmailException(ex.getMessage());
            }
        }
        else{
            log.debug("SendPasswordResetEmail: User \"" + request.getEmail() + "\" NOT found.");
        }

        return new ApiResponse("If user is registered, an email was sent. Check inbox");
    }

    private void sendEmail(ExtraUser user, PasswordResetToken token) throws MailException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String templateBody = "\n" +
                "<!doctype html>\n" +
                "<html lang=\"en-US\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n" +
                "    <title>Reset Password Email Template</title>\n" +
                "    <meta name=\"description\" content=\"Reset Password Email Template.\">\n" +
                "    <style type=\"text/css\">\n" +
                "        a:hover {text-decoration: underline !important;}\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body marginheight=\"0\" topmargin=\"0\" marginwidth=\"0\" style=\"margin: 0px; background-color: #f2f3f8;\" leftmargin=\"0\">\n" +
                "    <!--100% body table-->\n" +
                "    <table cellspacing=\"0\" border=\"0\" cellpadding=\"0\" width=\"100%\" bgcolor=\"#f2f3f8\"\n" +
                "        style=\"@import url(https://fonts.googleapis.com/css?family=Rubik:300,400,500,700|Open+Sans:300,400,600,700); font-family: 'Open Sans', sans-serif;\">\n" +
                "        <tr>\n" +
                "            <td>\n" +
                "                <table style=\"background-color: #f2f3f8; max-width:670px;  margin:0 auto;\" width=\"100%\" border=\"0\"\n" +
                "                    align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:80px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        \n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td>\n" +
                "                            <table width=\"95%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                style=\"max-width:670px;background:#fff; border-radius:3px; text-align:center;-webkit-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);-moz-box-shadow:0 6px 18px 0 rgba(0,0,0,.06);box-shadow:0 6px 18px 0 rgba(0,0,0,.06);\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"padding:0 35px;\">\n" +
                "                                        <h1 style=\"color:#1e1e2d; font-weight:500; margin:0;font-size:32px;font-family:'Rubik',sans-serif;\">You have\n" +
                "                                            requested to reset your password</h1>\n" +
                "                                        <span\n" +
                "                                            style=\"display:inline-block; vertical-align:middle; margin:29px 0 26px; border-bottom:1px solid #cecece; width:100px;\"></span>\n" +
                "                                        <p style=\"color:#455056; font-size:15px;line-height:24px; margin:0;\">\n" +
                "                                            We cannot simply send you your old password. A unique link to reset your\n" +
                "                                            password has been generated for you. To reset your password, click the\n" +
                "                                            following link and follow the instructions.\n" +
                "                                        </p>\n" +
                "                                        <a href=\"http://extra/reset-password/" + token.getId().toString() + "\"\n" +
                "                                            style=\"background:#20e277;text-decoration:none !important; font-weight:500; margin-top:35px; color:#fff;text-transform:uppercase; font-size:14px;padding:10px 24px;display:inline-block;border-radius:50px;\">Reset\n" +
                "                                            Password</a>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                                <tr>\n" +
                "                                    <td style=\"height:40px;\">&nbsp;</td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:20px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        \n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"height:80px;\">&nbsp;</td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "    <!--/100% body table-->\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        helper.setFrom("noreply.extraapp@gmail.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Extraapp - Password recovery");
        helper.setText(
                templateBody,
                true
        );

        mailSender.send(message);
    }

    public ApiResponse resetPassword(PasswordResetRequest request) throws InvalidPasswordResetTokenException{
        PasswordResetToken
                token = tokenRepository
                            .findById(request.getToken())
                            .orElseThrow(InvalidPasswordResetTokenException::new);

        token.assertValid();
        String newPassword = passwordEncoder.encode(request.getNewPassword());
        ExtraUser changingUser = token.getUser();
        changingUser.setPassword(newPassword);
        userRepository.save(changingUser);

        return new ApiResponse("Password changed successfully");
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public ApiResponse registerUserDevice(ExtraUser user, DeviceRegisteringRequest request) {
        Optional<UserDevice> foundDevice = deviceRepository.findByFcmToken(request.getToken());

        if (foundDevice.isPresent()) {
            log.debug("Device is already registered, updating user information");
            UserDevice device = foundDevice.get();
            device.setUser(user);
            device.setModified(new Timestamp(System.currentTimeMillis()));
        } else {
            log.debug("Device not registered. Registering...");
            deviceRepository.save(
                    new UserDevice(request.getToken(), user)
            );
        }

        return new ApiResponse("Device registered successfully");
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public ApiResponse unregisterUserDevice(DeviceUnregisteringRequest request) {
        if (request.getToken() == null) {
            return new ApiResponse("Device is not registered");
        }

        Optional<UserDevice> foundDevice =  deviceRepository.findByFcmToken(request.getToken());

        if (foundDevice.isEmpty()) {
            return new ApiResponse("Device is not registered");
        }

        deviceRepository.delete(foundDevice.get());
        return new ApiResponse("Device was removed successfully");
    }

}
