package pwr.zpibackend.controllers.thesis;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pwr.zpibackend.services.thesis.CommentService;

@RestController
@AllArgsConstructor
@RequestMapping("/thesis/comment")
public class CommentController {

    private final CommentService commentService;

}
