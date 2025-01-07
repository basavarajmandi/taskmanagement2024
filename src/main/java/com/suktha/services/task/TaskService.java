package com.suktha.services.task;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface TaskService {

     long getOverdueTaskCount();

     Map<String, Long> getTaskStatusCounts();


}
