package com.skapp.community.common.component.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skapp.community.common.component.AsyncEmailSender;
import com.skapp.community.common.constant.CommonMessageConstant;
import com.skapp.community.common.exception.ModuleException;
import com.skapp.community.common.model.OrganizationConfig;
import com.skapp.community.common.payload.response.EmailServerConfigResponseDto;
import com.skapp.community.common.repository.OrganizationConfigDao;
import com.skapp.community.common.service.EncryptionDecryptionService;
import com.skapp.community.common.type.EmailBodyTemplates;
import com.skapp.community.common.type.OrganizationConfigType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailSenderImpl implements AsyncEmailSender {

	@NonNull
	private final OrganizationConfigDao organizationConfigDao;

	@NonNull
	private final EncryptionDecryptionService encryptionDecryptionService;

	@NonNull
	private final ObjectMapper objectMapper;

	@Value("${encryptDecryptAlgorithm.secret}")
	private String encryptSecret;

	@Override
	public void sendMail(String to, String subject, String htmlBody, EmailBodyTemplates emailTemplate,
			Map<String, String> placeholders) {
		try {
			JavaMailSender emailSender = createJavaMailSender();
			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(htmlBody, true);

			emailSender.send(mimeMessage);
			log.info("Email sent successfully to {}", to);

		}
		catch (MessagingException e) {
			log.error("Error sending email: {}", e.getMessage());
		}
	}

	private JavaMailSender createJavaMailSender() {
		Optional<OrganizationConfig> optionalOrganizationConfig = organizationConfigDao
			.findOrganizationConfigByOrganizationConfigType(OrganizationConfigType.EMAIL_CONFIGS);

		if (optionalOrganizationConfig.isEmpty()) {
			log.error("Email configuration not found");
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_EMAIL_CONFIG_NOT_FOUND);
		}

		try {
			OrganizationConfig emailConfig = optionalOrganizationConfig.get();
			JsonNode configNode = objectMapper.readTree(emailConfig.getOrganizationConfigValue());

			EmailServerConfigResponseDto emailConfigDto = objectMapper.treeToValue(configNode,
					EmailServerConfigResponseDto.class);

			if (Boolean.FALSE.equals(emailConfigDto.getIsEnabled())) {
				log.error("Email service is not enabled");
				throw new ModuleException(CommonMessageConstant.COMMON_ERROR_EMAIL_CONFIG_NOT_FOUND);
			}

			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost(emailConfigDto.getEmailServiceProvider());
			mailSender.setPort(emailConfigDto.getPortNumber());
			mailSender.setUsername(emailConfigDto.getUsername());
			mailSender.setPassword(encryptionDecryptionService.decrypt(emailConfigDto.getAppPassword(), encryptSecret));

			Properties props = mailSender.getJavaMailProperties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.ssl.trust", emailConfigDto.getEmailServiceProvider());

			return mailSender;
		}
		catch (Exception e) {
			log.error("Error parsing email configuration", e);
			throw new ModuleException(CommonMessageConstant.COMMON_ERROR_EMAIL_CONFIG_NOT_FOUND);
		}
	}

}
