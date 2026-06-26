package com.example.demo.service;

import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class FavoritesService {

    public static final String FAVORITE_TASK_IDS = "favoriteTaskIds";

    public void addToFavorites(Long taskId, HttpSession session) {
        Set<Long> favoriteTaskIds = getFavoriteTaskIds(session);
        favoriteTaskIds.add(taskId);
        session.setAttribute(FAVORITE_TASK_IDS, favoriteTaskIds);
    }

    public void removeFromFavorites(Long taskId, HttpSession session) {
        Set<Long> favoriteTaskIds = getFavoriteTaskIds(session);
        favoriteTaskIds.remove(taskId);
        session.setAttribute(FAVORITE_TASK_IDS, favoriteTaskIds);
    }

    @SuppressWarnings("unchecked")
    public Set<Long> getFavoriteTaskIds(HttpSession session) {
        Object favoriteTaskIds = session.getAttribute(FAVORITE_TASK_IDS);
        if (favoriteTaskIds instanceof Set<?> storedIds) {
            return new LinkedHashSet<>((Set<Long>) storedIds);
        }
        return new LinkedHashSet<>();
    }
}
