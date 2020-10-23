export const addZeroToDateElement = (element) => {
    if (element < 10) {
        return "0" + element;
    } else return element;
};

export const addMinutes = (date, minutes) => {
    return new Date(date.getTime() + minutes * 60000);
};

export const getDateToRequest= (date) => {
    const year = date.getFullYear();
    const month = addZeroToDateElement(date.getMonth() +1);
    const day = addZeroToDateElement(date.getDate());
    const hours = addZeroToDateElement(date.getHours());
    const minutes = addZeroToDateElement(date.getMinutes());
    return `${year}-${month}-${day}T${hours}:${minutes}`;
};