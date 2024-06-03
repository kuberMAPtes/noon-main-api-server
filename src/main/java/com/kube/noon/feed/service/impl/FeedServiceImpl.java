package com.kube.noon.feed.service.impl;

import com.kube.noon.feed.entity.Feed;
import com.kube.noon.feed.repository.FeedRepository;
import com.kube.noon.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedRepository feedRepository;

    @Override
    public List<Feed> getFeedList() throws IOException {
        return feedRepository.findAll();
    }
}
