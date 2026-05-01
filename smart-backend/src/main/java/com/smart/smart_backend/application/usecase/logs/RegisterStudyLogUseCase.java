package com.smart.smart_backend.application.usecase.logs;

import com.smart.smart_backend.application.dto.habit.log.StudyLogRequestDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.application.mapper.logs.StudyLogMapper;
import com.smart.smart_backend.application.port.in.logs.RegisterStudyLog;
import com.smart.smart_backend.application.port.out.logs.StudyLogRepositoryPort;
import com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.exception.EntryNotFoundException;
import com.smart.smart_backend.domain.model.habit.StudyLog;

public class RegisterStudyLogUseCase implements RegisterStudyLog {

        private final DailyEntryRepositoryPort dailyEntryRepositoryPort;
        private final StudyLogRepositoryPort studyLogRepositoryPort;

        public RegisterStudyLogUseCase(DailyEntryRepositoryPort dailyEntryRepositoryPort,
                        StudyLogRepositoryPort studyLogRepositoryPort) {
                this.dailyEntryRepositoryPort = dailyEntryRepositoryPort;
                this.studyLogRepositoryPort = studyLogRepositoryPort;
        }

        @Override
        public StudyLogResponseDto execute(Long userId, StudyLogRequestDto requestDto) {

                dailyEntryRepositoryPort.findByIdAndUserId(requestDto.entryId(), userId)
                                .orElseThrow(() -> new EntryNotFoundException(requestDto.entryId()));

                StudyLog log = studyLogRepositoryPort.findByEntryId(
                                requestDto.entryId())
                                .map(existing -> existing.update(
                                                requestDto.studied(), requestDto.hours(),
                                                requestDto.subject(), requestDto.skipReason()))
                                .orElseGet(() -> StudyLog.create(
                                                requestDto.entryId(),
                                                requestDto.studied(), requestDto.hours(),
                                                requestDto.subject(), requestDto.skipReason()));

                StudyLog save = studyLogRepositoryPort.save(log);
                return StudyLogMapper.toResponse(save);
        }

}
