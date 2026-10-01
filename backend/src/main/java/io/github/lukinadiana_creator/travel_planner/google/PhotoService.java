package io.github.lukinadiana_creator.travel_planner.google;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PhotoService {
    public String createPhotoUrl(String photoName) {
        return "https://places.googleapis.com/v1/" + photoName + "/media?maxHeightPx=800&key=AIzaSyATvM4Wu76_auqGucNL9O-aPanOB6el8JE";
    }
}
