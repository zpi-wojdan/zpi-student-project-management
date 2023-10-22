function getLoggedUser() {
    const loggedInUser = localStorage.getItem("user");
    // @ts-ignore
    return JSON.parse(loggedInUser);
}

export default getLoggedUser