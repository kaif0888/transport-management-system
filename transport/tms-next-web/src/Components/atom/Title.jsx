export default function Title({title, className}){
    return(
        <h1 className={`${className} text-2xl sm:text-4xl w-full }`}>{title}</h1>
    );
}