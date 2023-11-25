import { Employee } from "../user/Employee";
import { Thesis } from "./Thesis";

export type Comment = {
    id: number;
    author: Employee;
    content: string;
    creationTime: number;
    thesis: Thesis;
}

export type CommentDTO = {
    content: string;
    authorId: number;
    thesisId: number;
}
